package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话响应
 *
 * @author 15331
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会话响应")
public class SessionResponse {

    @Schema(description = "会话ID")
    private Integer id;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "AI模型名称")
    private String modelName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
