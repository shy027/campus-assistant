package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息请求
 *
 * @author 15331
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateUserRequest {

    @Schema(description = "用户名")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;

    @Schema(description = "密码")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证至少有一个字段不为空
     */
    public boolean hasAtLeastOneField() {
        return username != null || password != null || email != null;
    }
}
