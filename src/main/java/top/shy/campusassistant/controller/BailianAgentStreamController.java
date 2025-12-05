package top.shy.campusassistant.controller;

import java.util.List;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器类，用于处理百炼平台智能体的流式请求。
 *
 * @author moqi
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class BailianAgentStreamController {
    /**
     * 智能体实例，用于与 DashScope 平台交互。
     */
    private final DashScopeAgent agent;

    /**
     * 应用 ID，从配置文件中读取。
     */
    @Value("${spring.ai.dashscope.agent.scholarship-app-id}")
    private String appId;

    /**
     * 构造方法，初始化 DashScopeAgent 实例并设置相关选项。
     *
     * @param dashscopeAgentApi DashScopeAgentApi 实例，用于与 DashScope 接口通信
     */
    public BailianAgentStreamController(DashScopeAgentApi dashscopeAgentApi) {
        // 初始化 agent，并启用增量输出和思考过程显示功能
        this.agent = new DashScopeAgent(dashscopeAgentApi,
                DashScopeAgentOptions.builder()
                        .withSessionId("currentSessionId")
                        .withIncrementalOutput(true)
                        .withHasThoughts(true)
                        .build());
    }

    /**
     * 处理流式聊天请求，接收用户消息并返回模型逐步生成的内容。
     *
     * @param message 用户输入的消息，默认值为“你好，请问你的知识库文档主要是关于什么内容的?”
     * @return 返回一个 Flux 流，其中每一项是模型逐步生成的文本内容
     */
    @GetMapping(value = "/bailian/agent/stream", produces = "text/event-stream")
    public Flux<String> stream(@RequestParam(value = "message", defaultValue = "你好，请问你的知识库文档主要是关于什么内容的?") String message) {
        // 构建选项和提示词
        DashScopeAgentOptions options = DashScopeAgentOptions.builder().withAppId(appId).build();
        Prompt prompt = new Prompt(message, options);

        // 调用 agent 的流式接口，并对响应进行解析和日志记录
        return agent.stream(prompt).mapNotNull(response -> {
            if (response == null) {
                log.error("聊天响应为空");
                return "聊天响应为空";
            } else {
                response.getResult();
            }

            // 提取助手回复的主要内容
            AssistantMessage appOutput = response.getResult().getOutput();
            String content = appOutput.getText();

            // 获取引用文档及思考过程信息（可用于调试或展示）
            DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) appOutput.getMetadata().get("output");
            List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
            List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();

            // 打印生成的内容到日志
            log.info("内容:\n{}\n\n", content);

            // 输出助手回复的主要内容
            return content;
        });
    }
}
