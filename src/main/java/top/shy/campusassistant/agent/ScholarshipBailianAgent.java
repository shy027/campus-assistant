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
 * 奖助百炼智能体Agent
 * 专门处理奖学金、助学金相关的问题
 *
 * @author 15331
 */
@Slf4j
@Component
public class ScholarshipBailianAgent implements BaseAgent {

    private final DashScopeAgent agent;
    private final List<String> keywords;

    @Value("${spring.ai.dashscope.agent.scholarship-app-id}")
    private String appId;

    public ScholarshipBailianAgent(DashScopeAgentApi dashscopeAgentApi) {
        this.agent = new DashScopeAgent(dashscopeAgentApi);
        this.keywords = Arrays.asList(
            "奖学金", "助学金", "海燕", "励志", "黄宝华", "黄炎培",
            "奖助", "补助", "资助", "评选", "申请"
        );
    }

    @Override
    public AssistantMessage handle(String message, RunnableConfig config) {
        try {
            log.info("{}正在处理消息: {}", getAgentName(), message);
            
            // 构建选项和提示词
            DashScopeAgentOptions options = DashScopeAgentOptions.builder()
                .withAppId(appId)
                .build();
            Prompt prompt = new Prompt(message, options);

            // 执行调用
            ChatResponse response = agent.call(prompt);

            if (response == null || response.getResult() == null) {
                log.error("聊天响应为空");
                throw new RuntimeException("聊天响应为空");
            }

            // 获取主要输出内容
            AssistantMessage appOutput = response.getResult().getOutput();
            log.info("{}处理完成，响应: {}", getAgentName(), appOutput.getText());
            
            return appOutput;
        } catch (Exception e) {
            log.error("{}处理消息失败", getAgentName(), e);
            throw new RuntimeException("Agent处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式处理用户消息
     *
     * @param message 用户消息
     * @param config 运行配置
     * @return 流式响应
     */
    public Flux<String> streamHandle(String message, RunnableConfig config) {
        try {
            log.info("{}正在流式处理消息: {}", getAgentName(), message);
            
            // 构建选项和提示词
            DashScopeAgentOptions options = DashScopeAgentOptions.builder()
                .withAppId(appId)
                .withIncrementalOutput(true)
                .withHasThoughts(true)
                .build();
            Prompt prompt = new Prompt(message, options);

            // 执行流式调用
            return agent.stream(prompt).mapNotNull(response -> {
                if (response == null || response.getResult() == null) {
                    log.error("聊天响应为空");
                    return null;
                }

                // 提取助手回复的主要内容
                AssistantMessage appOutput = response.getResult().getOutput();
                String content = appOutput.getText();
                
                log.debug("{}流式输出: {}", getAgentName(), content);
                return content;
            });
        } catch (Exception e) {
            log.error("{}流式处理消息失败", getAgentName(), e);
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
        return "ScholarshipBailianAgent";
    }

    @Override
    public String getDescription() {
        return "奖助智能体，处理奖学金、助学金相关问题";
    }
}
