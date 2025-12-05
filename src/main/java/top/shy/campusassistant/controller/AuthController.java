package top.shy.campusassistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.shy.campusassistant.common.result.Result;
import top.shy.campusassistant.dto.LoginByPasswordRequest;
import top.shy.campusassistant.dto.LoginBySmsRequest;
import top.shy.campusassistant.dto.LoginResponse;
import top.shy.campusassistant.dto.SendSmsRequest;
import top.shy.campusassistant.service.AuthService;

/**
 * 认证控制器
 *
 * @author 15331
 */
@Tag(name = "认证管理", description = "用户登录、登出、验证码相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "密码登录", description = "使用手机号和密码登录（需先注册且设置密码）")
    @PostMapping("/login/password")
    public Result<LoginResponse> loginByPassword(@Valid @RequestBody LoginByPasswordRequest request) {
        LoginResponse response = authService.loginByPassword(request);
        return Result.ok(response);
    }

    @Operation(summary = "短信验证码登录", description = "使用手机号和验证码登录（首次登录自动注册）")
    @PostMapping("/login/sms")
    public Result<LoginResponse> loginBySms(@Valid @RequestBody LoginBySmsRequest request) {
        LoginResponse response = authService.loginBySms(request);
        return Result.ok(response);
    }

    @Operation(summary = "发送短信验证码", description = "发送登录验证码到指定手机号")
    @PostMapping("/send-sms")
    public Result<Void> sendSmsCode(@Valid @RequestBody SendSmsRequest request) {
        authService.sendSmsCode(request.getPhone());
        return Result.ok();
    }

    @Operation(summary = "登出", description = "退出登录，清除 Token")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }
}
