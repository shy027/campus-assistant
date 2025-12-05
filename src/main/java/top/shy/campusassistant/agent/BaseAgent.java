package top.shy.campusassistant.agent;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import org.springframework.ai.chat.messages.AssistantMessage;

/**
 * Agent基类接口
 * 定义所有Agent的通用行为
 *
 * @author 15331
 */
public interface BaseAgent {

    /**
     * 处理用户消息
     *
     * @param message 用户消息
     * @param config 运行配置
     * @return AI响应
     */
    AssistantMessage handle(String message, RunnableConfig config);

    /**
     * 判断是否能处理该消息
     *
     * @param message 用户消息
     * @return true表示可以处理
     */
    boolean canHandle(String message);

    /**
     * 获取Agent名称
     *
     * @return Agent名称
     */
    String getAgentName();

    /**
     * 获取Agent描述
     *
     * @return Agent描述
     */
    String getDescription();
}
