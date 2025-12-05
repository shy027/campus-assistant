package top.shy.campusassistant.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 15331
 */
@Data
public class RequestDTO implements Serializable {


    /** 用户唯一标识，用于区分会话 */
    private String userId;

    /** 用户本次输入的问题 */
    private String message;

    /** 会话ID（可选，不传则创建新会话） */
    private Integer sessionId;
}