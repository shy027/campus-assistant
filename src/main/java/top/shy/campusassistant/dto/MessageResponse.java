package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息响应
 *
 * @author 15331
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息响应")
public class MessageResponse {

    @Schema(description = "消息ID")
    private Integer id;

    @Schema(description = "会话ID")
    private Integer sessionId;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "消息角色（user/ai/system）")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "AI模型名称")
    private String modelName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
