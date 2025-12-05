package top.shy.campusassistant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.shy.campusassistant.common.exception.ServerException;
import top.shy.campusassistant.common.result.ResultCode;
import top.shy.campusassistant.common.cache.RedisCache;
import top.shy.campusassistant.common.cache.RedisKeys;
import top.shy.campusassistant.common.utils.JwtUtils;
import top.shy.campusassistant.common.utils.RongLianSmsUtils;
import top.shy.campusassistant.common.utils.SecurityUtils;
import top.shy.campusassistant.dto.LoginByPasswordRequest;
import top.shy.campusassistant.dto.LoginBySmsRequest;
import top.shy.campusassistant.dto.LoginResponse;
import top.shy.campusassistant.entity.User;
import top.shy.campusassistant.service.AuthService;
import top.shy.campusassistant.service.UserService;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 *
 * @author mqxu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisCache redisCache;
    private final RongLianSmsUtils rongLianSmsUtils;

    /**
     * 短信验证码有效期（分钟）
     */
    private static final int SMS_CODE_EXPIRE_MINUTES = 5;

    /**
     * Token 有效期（秒）
     */
    private static final int TOKEN_EXPIRE_SECONDS = 86400; // 24小时

    @Override
    public LoginResponse loginByPassword(LoginByPasswordRequest request) {
        String phone = request.getPhone();
        String password = request.getPassword();

        // 查询用户
        User user = userService.getByPhone(phone);
        if (user == null) {
            log.warn("密码登录失败，用户不存在，手机号：{}", phone);
            throw new ServerException("用户不存在，请先使用短信验证码登录");
        }

        // 检查用户是否设置了密码
        if (!StringUtils.hasText(user.getPassword())) {
            log.warn("密码登录失败，用户未设置密码，手机号：{}", phone);
            throw new ServerException("您还未设置密码，请使用短信验证码登录");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密码登录失败，密码错误，手机号：{}", phone);
            throw new ServerException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("密码登录失败，用户已被禁用，手机号：{}", phone);
            throw new ServerException("账号已被禁用，请联系管理员");
        }

        // 生成 Token 并返回
        return generateTokenAndResponse(user);
    }

    @Override
    public LoginResponse loginBySms(LoginBySmsRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();

        // 验证验证码
        String cacheKey = RedisKeys.getSmsCodeKey(phone);
        String cachedCode = redisCache.get(cacheKey, String.class);

        if (!StringUtils.hasText(cachedCode)) {
            log.warn("短信登录失败，验证码已过期或不存在，手机号：{}", phone);
            throw new ServerException("验证码已过期，请重新获取");
        }

        if (!code.equals(cachedCode)) {
            log.warn("短信登录失败，验证码错误，手机号：{}，输入：{}，正确：{}", phone, code, cachedCode);
            throw new ServerException("验证码错误");
        }

        // 验证通过，删除验证码
        redisCache.delete(cacheKey);

        // 查询用户，不存在则创建（首次登录即注册）
        User user = userService.getByPhone(phone);
        if (user == null) {
            log.info("首次登录，创建新用户，手机号：{}", phone);
            user = userService.createUser(phone, null);
        } else {
            // 检查用户状态
            if (user.getStatus() == 0) {
                log.warn("短信登录失败，用户已被禁用，手机号：{}", phone);
                throw new ServerException("账号已被禁用，请联系管理员");
            }
        }

        // 生成 Token 并返回
        return generateTokenAndResponse(user);
    }

    @Override
    public void sendSmsCode(String phone) {
        // 生成6位随机验证码
        String code = String.format("%06d", (int)(Math.random() * 1000000));

        // 发送短信
        boolean success = rongLianSmsUtils.sendSmsCode(phone, code);
        if (!success) {
            log.error("短信发送失败，手机号：{}", phone);
            throw new ServerException("短信发送失败，请稍后重试");
        }

        // 存入 Redis，5分钟过期
        String cacheKey = RedisKeys.getSmsCodeKey(phone);
        redisCache.set(cacheKey, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        log.info("短信验证码发送成功，手机号：{}，验证码：{}（仅开发环境打印）", phone, code);
    }

    @Override
    public void logout() {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 删除 Redis 中的 Token
        String tokenKey = RedisKeys.getUserTokenKey(userId);
        redisCache.delete(tokenKey);

        log.info("用户登出成功，用户ID：{}", userId);
    }

    /**
     * 生成 Token 并构建响应
     */
    private LoginResponse generateTokenAndResponse(User user) {
        // 更新最后登录时间
        userService.updateLastLoginTime(user.getId());

        // 生成 JWT Token
        String token = jwtUtils.generateToken(user.getId().longValue());

        // 将 Token 存入 Redis
        String tokenKey = RedisKeys.getUserTokenKey(user.getId().longValue());
        redisCache.set(tokenKey, token, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        log.info("用户登录成功，手机号：{}，用户ID：{}", user.getPhone(), user.getId());

        // 构建响应
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .build();
    }
}
