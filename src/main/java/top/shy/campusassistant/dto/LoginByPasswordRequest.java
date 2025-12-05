package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码登录请求
 *
 * @author mqxu
 */
@Data
@Schema(description = "密码登录请求")
public class LoginByPasswordRequest {

    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
