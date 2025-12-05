package top.shy.campusassistant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.shy.campusassistant.entity.User;
import top.shy.campusassistant.mapper.UserMapper;
import top.shy.campusassistant.service.UserService;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 *
 * @author mqxu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public User createUser(String phone, String password) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setUsername("用户" + phone.substring(7)); // 默认用户名：用户+后4位手机号
        user.setStatus(1); // 启用状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);

        userMapper.insert(user);
        log.info("创建新用户成功，手机号：{}, 用户ID：{}", phone, user.getId());
        return user;
    }

    @Override
    public void updateLastLoginTime(Integer userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public User getById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User updateUserInfo(Integer userId, String username, String password, String email) {
        // 查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            log.error("用户不存在，用户ID：{}", userId);
            throw new RuntimeException("用户不存在");
        }

        // 创建更新对象
        User updateUser = new User();
        updateUser.setId(userId);
        
        // 更新用户名
        if (username != null && !username.trim().isEmpty()) {
            updateUser.setUsername(username);
        }
        
        // 更新密码（需要加密）
        if (password != null && !password.trim().isEmpty()) {
            updateUser.setPassword(passwordEncoder.encode(password));
        }
        
        // 更新邮箱
        if (email != null && !email.trim().isEmpty()) {
            updateUser.setEmail(email);
        }
        
        // 更新时间
        updateUser.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        userMapper.updateById(updateUser);
        log.info("更新用户信息成功，用户ID：{}", userId);
        
        // 返回更新后的用户信息
        return userMapper.selectById(userId);
    }

    @Override
    public User updateAvatar(Integer userId, String avatarUrl) {
        // 查询用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            log.error("用户不存在，用户ID：{}", userId);
            throw new RuntimeException("用户不存在");
        }

        // 创建更新对象
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setAvatar(avatarUrl);
        updateUser.setUpdateTime(LocalDateTime.now());

        // 执行更新
        userMapper.updateById(updateUser);
        log.info("更新用户头像成功，用户ID：{}，头像URL：{}", userId, avatarUrl);

        // 返回更新后的用户信息
        return userMapper.selectById(userId);
    }
}
