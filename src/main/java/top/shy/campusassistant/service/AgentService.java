package top.shy.campusassistant.service;

import reactor.core.publisher.Flux;
import top.shy.campusassistant.model.AssistantResponse;

/**
 * Agent服务接口
 *
 * @author 15331
 */
public interface AgentService {

    /**
     * 处理聊天请求
     *
     * @param userId 用户ID
     * @param message 用户消息
     * @param sessionId 会话ID(可选)
     * @return AI响应
     */
    AssistantResponse chat(Integer userId, String message, Integer sessionId);

    /**
     * 流式处理聊天请求
     *
     * @param userId 用户ID
     * @param message 用户消息
     * @param sessionId 会话ID(可选)
     * @return 流式AI响应
     */
    Flux<String> streamChat(Integer userId, String message, Integer sessionId);

    /**
     * 获取用户的历史记录
     *
     * @param userId 用户ID
     * @return 历史记录信息
     */
    String getHistory(String userId);
}
