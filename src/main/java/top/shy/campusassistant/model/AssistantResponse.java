package top.shy.campusassistant.model;

import lombok.Data;

/**
 * @author 15331
 */
@Data
public class AssistantResponse {
    // 用户id
    private String userId;
    // 线程id
    private String threadId;
    // 会话ID
    private Integer sessionId;
    // 主要回答内容
    private String answer;
    // 相关建议
    private String suggestion;
    // 回答类型：weather/course/library/general
    private String type;
    // 是否需要进一步帮助
    private boolean needsFurtherHelp;
}
