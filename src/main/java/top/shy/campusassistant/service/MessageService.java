package top.shy.campusassistant.service;

import top.shy.campusassistant.dto.MessageResponse;
import top.shy.campusassistant.entity.Message;

import java.util.List;

/**
 * 消息服务接口
 *
 * @author 15331
 */
public interface MessageService {

    /**
     * 创建消息
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param role 消息角色（user/ai/system）
     * @param content 消息内容
     * @param modelName 模型名称
     * @return 消息信息
     */
    Message createMessage(Integer sessionId, Integer userId, String role, String content, String modelName);

    /**
     * 根据会话ID查询消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<MessageResponse> getMessagesBySessionId(Integer sessionId);
}
