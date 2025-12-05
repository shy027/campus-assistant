package top.shy.campusassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传头像响应
 *
 * @author 15331
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "上传头像响应")
public class UploadAvatarResponse {

    @Schema(description = "头像URL")
    private String avatarUrl;
}
