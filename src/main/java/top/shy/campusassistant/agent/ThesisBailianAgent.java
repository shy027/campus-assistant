package top.shy.campusassistant.agent;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * 论文助手智能体（百炼）
 * 处理论文写作、选题、引用等学术问题
 */
@Slf4j
@Component
public class ThesisBailianAgent implements BaseAgent {

    private final DashScopeAgent agent;
    private final List<String> keywords;

    @Value("${spring.ai.dashscope.agent.thesis-app-id}")
    private String appId;

    public ThesisBailianAgent(DashScopeAgentApi dashscopeAgentApi) {
        this.agent = new DashScopeAgent(dashscopeAgentApi);
        this.keywords = Arrays.asList(
                "论文", "选题", "写作", "引用", "参考文献",
                "摘要", "方法", "实验", "科研", "投稿"
        );
    }

    @Override
    public AssistantMessage handle(String message, RunnableConfig config) {
        try {
            log.info("{} 正在处理消息: {}", getAgentName(), message);

            DashScopeAgentOptions options = DashScopeAgentOptions.builder()
                    .withAppId(appId)
                    .build();
            Prompt prompt = new Prompt(message, options);

            ChatResponse response = agent.call(prompt);
            if (response == null || response.getResult() == null) {
                log.error("论文助手聊天响应为空");
                throw new RuntimeException("聊天响应为空");
            }

            AssistantMessage appOutput = response.getResult().getOutput();
            log.info("{} 处理完成，响应: {}", getAgentName(), appOutput.getText());
            return appOutput;
        } catch (Exception e) {
            log.error("{} 处理消息失败", getAgentName(), e);
            throw new RuntimeException("Agent处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式处理
     */
    public Flux<String> streamHandle(String message, RunnableConfig config) {
        try {
            log.info("{} 正在流式处理消息: {}", getAgentName(), message);

            DashScopeAgentOptions options = DashScopeAgentOptions.builder()
                    .withAppId(appId)
                    .withIncrementalOutput(true)
                    .withHasThoughts(true)
                    .build();
            Prompt prompt = new Prompt(message, options);

            return agent.stream(prompt).mapNotNull(response -> {
                if (response == null || response.getResult() == null) {
                    log.error("论文助手聊天响应为空");
                    return null;
                }
                AssistantMessage appOutput = response.getResult().getOutput();
                String content = appOutput.getText();
                log.debug("{} 流式输出: {}", getAgentName(), content);
                return content;
            });
        } catch (Exception e) {
            log.error("{} 流式处理消息失败", getAgentName(), e);
            return Flux.error(new RuntimeException("Agent流式处理失败: " + e.getMessage(), e));
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

    @Override
    public String getAgentName() {
        return "ThesisBailianAgent";
    }

    @Override
    public String getDescription() {
        return "论文助手，处理学术写作与研究问题";
    }
}
