package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 短信登录请求
 *
 * @author mqxu
 */
@Data
@Schema(description = "短信登录请求")
public class LoginBySmsRequest {

    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String code;
}
