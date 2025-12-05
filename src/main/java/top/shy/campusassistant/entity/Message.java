package top.shy.campusassistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息实体
 *
 * @author 15331
 */
@Data
@TableName("message")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 会话ID
     */
    private Integer sessionId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 消息角色（user-用户，ai-AI助手，system-系统）
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 使用的AI模型
     */
    private String modelName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
