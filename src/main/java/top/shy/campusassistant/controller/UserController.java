package top.shy.campusassistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.shy.campusassistant.common.result.Result;
import top.shy.campusassistant.common.utils.SecurityUtils;
import top.shy.campusassistant.dto.UpdateUserRequest;
import top.shy.campusassistant.dto.UploadAvatarResponse;
import top.shy.campusassistant.dto.UserInfoResponse;
import top.shy.campusassistant.entity.User;
import top.shy.campusassistant.service.FileUploadService;
import top.shy.campusassistant.service.UserService;

/**
 * 用户管理控制器
 *
 * @author 15331
 */
@Tag(name = "用户管理", description = "用户信息查询和修改相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileUploadService fileUploadService;

    @Operation(summary = "根据ID查询用户信息", description = "根据用户ID查询用户详细信息（无需登录）")
    @GetMapping("/{id}")
    public Result<UserInfoResponse> getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        UserInfoResponse response = convertToResponse(user);
        return Result.ok(response);
    }

    @Operation(summary = "根据手机号查询用户信息", description = "根据手机号查询用户详细信息（无需登录）")
    @GetMapping("/phone/{phone}")
    public Result<UserInfoResponse> getUserByPhone(@PathVariable String phone) {
        User user = userService.getByPhone(phone);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        UserInfoResponse response = convertToResponse(user);
        return Result.ok(response);
    }

    @Operation(summary = "修改用户信息", description = "修改当前登录用户的信息（用户名、密码、邮箱），需要登录")
    @PutMapping
    public Result<UserInfoResponse> updateUserInfo(@Valid @RequestBody UpdateUserRequest request) {
        // 验证至少有一个字段
        if (!request.hasAtLeastOneField()) {
            return Result.fail("至少需要提供一个要更新的字段");
        }

        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 更新用户信息
        User updatedUser = userService.updateUserInfo(
                currentUserId.intValue(),
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );

        UserInfoResponse response = convertToResponse(updatedUser);
        return Result.ok(response);
    }

    @Operation(summary = "上传头像", description = "上传用户头像图片到OSS，需要登录")
    @PostMapping("/avatar")
    public Result<UploadAvatarResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 上传文件到OSS
        String avatarUrl = fileUploadService.uploadAvatar(file, currentUserId.intValue());

        // 更新用户头像URL
        userService.updateAvatar(currentUserId.intValue(), avatarUrl);

        // 返回头像URL
        UploadAvatarResponse response = UploadAvatarResponse.builder()
                .avatarUrl(avatarUrl)
                .build();
        return Result.ok(response);
    }

    /**
     * 将User实体转换为UserInfoResponse
     */
    private UserInfoResponse convertToResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }
}
