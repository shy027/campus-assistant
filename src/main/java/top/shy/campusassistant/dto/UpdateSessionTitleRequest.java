package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新会话标题请求
 *
 * @author 15331
 */
@Data
@Schema(description = "更新会话标题请求")
public class UpdateSessionTitleRequest {

    @Schema(description = "会话标题")
    @NotBlank(message = "会话标题不能为空")
    @Size(max = 100, message = "会话标题长度不能超过100个字符")
    private String title;
}
