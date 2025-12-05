package top.shy.campusassistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话实体
 *
 * @author 15331
 */
@Data
@TableName("session")
public class Session implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 用户ID
     */
    private Integer userId;

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
