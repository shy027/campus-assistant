package top.shy.campusassistant.controller;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 百炼智能体控制器，用于处理与 DashScope 智能体的交互。
 * <p>
 * 提供一个 REST 接口，通过该接口可以向 DashScope Agent 发送消息并获取响应结果。
 * 支持非流式调用方式。
 *
 * @author 15331
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class BailianAgentController {
    /**
     * DashScope 智能体实例，用于执行实际的 AI 对话逻辑。
     */
    private final DashScopeAgent agent;

    /**
     * 应用 ID，从配置文件中读取，用于标识当前使用的 DashScope 应用。
     */
    @Value("${spring.ai.dashscope.agent.scholarship-app-id}")
    private String appId;

    /**
     * 构造方法，初始化 DashScopeAgent 实例。
     *
     * @param dashscopeAgentApi DashScopeAgentApi 实例，提供底层通信能力
     */
    public BailianAgentController(DashScopeAgentApi dashscopeAgentApi) {
        this.agent = new DashScopeAgent(dashscopeAgentApi);
    }


    /**
     * 向 DashScope Agent 发起一次对话请求，并返回响应内容。
     * <p>
     * 此方法会构建一个 Prompt 请求对象，发送给 DashScope Agent 并解析其响应，
     * 包括输出文本、文档引用和思考过程等信息，并记录日志。
     *
     * @param message 用户输入的消息，默认值为“国家奖学金评选有什么条件?”
     * @return 响应中的纯文本内容（AssistantMessage 的 text 字段）
     */
    @GetMapping("/bailian/agent/call")
    public String call(@RequestParam(value = "message", defaultValue = "国家奖学金评选有什么条件?") String message) {
        // 构建选项和提示词
        DashScopeAgentOptions options = DashScopeAgentOptions.builder().withAppId(appId).build();
        Prompt prompt = new Prompt(message, options);

        // 执行调用
        ChatResponse response = agent.call(prompt);

        // 处理空响应情况
        if (response == null) {
            log.error("聊天响应为空");
            return "聊天响应为空";
        } else {
            response.getResult();
        }

        // 获取主要输出内容
        AssistantMessage appOutput = response.getResult().getOutput();
        String content = appOutput.getText();

        // 解析元数据中的详细输出结构：包括文档引用和推理步骤
        // 嵌套类结构复杂: DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput 是深度嵌套的类结构
        // 保证类型安全: Java 要求明确指定泛型类型以确保编译时类型检查，使用完整的类路径防止与其他同名类产生冲突
        DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output = (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput) appOutput.getMetadata().get("output");
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference> docReferences = output.docReferences();
        List<DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts> thoughts = output.thoughts();

        // 记录主内容到日志
        log.info("内容:\n{}\n\n", content);

        // 输出文档引用信息
        if (docReferences != null && !docReferences.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputDocReference docReference : docReferences) {
                log.info("{}\n\n", docReference);
            }
        }

        // 输出推理过程信息
        if (thoughts != null && !thoughts.isEmpty()) {
            for (DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput.DashScopeAgentResponseOutputThoughts thought : thoughts) {
                log.info("{}\n\n", thought);
            }
        }

        // 返回纯文本内容
        return content;
    }
}
