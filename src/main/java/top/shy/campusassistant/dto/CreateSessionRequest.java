package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建会话请求
 *
 * @author 15331
 */
@Data
@Schema(description = "创建会话请求")
public class CreateSessionRequest {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
}
