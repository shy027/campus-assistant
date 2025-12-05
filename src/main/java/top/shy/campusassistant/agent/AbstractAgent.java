package top.shy.campusassistant.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallback;

import java.util.Arrays;
import java.util.List;

/**
 * Agent抽象基类
 * 提供Agent的通用实现
 *
 * @author 15331
 */
@Slf4j
public abstract class AbstractAgent implements BaseAgent {

    protected final ReactAgent reactAgent;
    protected final List<String> keywords;

    protected AbstractAgent(DashScopeChatModel chatModel, 
                          String systemPrompt, 
                          List<String> keywords,
                          ToolCallback... tools) {
        this.keywords = keywords;
        this.reactAgent = ReactAgent.builder()
                .name(getAgentName())
                .model(chatModel)
                .systemPrompt(systemPrompt)
                .tools(tools)
                .saver(new MemorySaver())
                .build();
    }

    @Override
    public AssistantMessage handle(String message, RunnableConfig config) {
        try {
            log.info("{}正在处理消息: {}", getAgentName(), message);
            return reactAgent.call(message, config);
        } catch (GraphRunnerException e) {
            log.error("{}处理消息失败", getAgentName(), e);
            throw new RuntimeException("Agent处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean canHandle(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        String lowerMessage = message.toLowerCase();
        return keywords.stream().anyMatch(lowerMessage::contains);
    }

    /**
     * 创建关键词列表的辅助方法
     */
    protected static List<String> createKeywords(String... keywords) {
        return Arrays.asList(keywords);
    }
}
