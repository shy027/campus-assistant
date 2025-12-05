package top.shy.campusassistant.service;

import top.shy.campusassistant.dto.LoginByPasswordRequest;
import top.shy.campusassistant.dto.LoginBySmsRequest;
import top.shy.campusassistant.dto.LoginResponse;

/**
 * 认证服务接口
 *
 * @author mqxu
 */
public interface AuthService {

    /**
     * 密码登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token）
     */
    LoginResponse loginByPassword(LoginByPasswordRequest request);

    /**
     * 短信验证码登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token）
     */
    LoginResponse loginBySms(LoginBySmsRequest request);

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    void sendSmsCode(String phone);

    /**
     * 登出
     */
    void logout();
}
