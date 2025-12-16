package top.shy.campusassistant.controller;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.shy.campusassistant.agent.BaseAgent;
import top.shy.campusassistant.agent.CourseBailianAgent;
import top.shy.campusassistant.agent.ScholarshipBailianAgent;
import top.shy.campusassistant.agent.ThesisBailianAgent;
import top.shy.campusassistant.agent.TravelBailianAgent;
import top.shy.campusassistant.model.AssistantResponse;
import top.shy.campusassistant.model.RequestDTO;
import top.shy.campusassistant.service.MessageService;
import top.shy.campusassistant.service.SessionService;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 百炼智能体统一控制器（非流式 + 流式）
 * 四个智能体：奖助助手、课程助手、出行助手、论文助手
 */
@Tag(name = "百炼智能体", description = "四个百炼智能体专属对话接口")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class BailianAgentUnifiedController {

    private final ScholarshipBailianAgent scholarshipBailianAgent;
    private final CourseBailianAgent courseBailianAgent;
    private final TravelBailianAgent travelBailianAgent;
    private final ThesisBailianAgent thesisBailianAgent;
    private final SessionService sessionService;
    private final MessageService messageService;

    /**
     * 非流式接口
     */
    @PostMapping("/{agentType}/chat")
    public AssistantResponse chat(@PathVariable String agentType, @RequestBody RequestDTO request) {
        Integer userId = Integer.parseInt(request.getUserId());
        String message = request.getMessage();
        Integer sessionId = request.getSessionId();

        BaseAgent agent = resolveAgent(agentType);
        String modelName = normalizeAgentType(agentType);
        Integer finalSessionId = ensureSession(userId, sessionId, modelName);

        // 记录用户消息
        messageService.createMessage(finalSessionId, userId, "user", message, modelName);

        // 构造配置，附带元数据
        String threadId = UUID.randomUUID().toString();
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", String.valueOf(userId))
                .addMetadata("session_id", String.valueOf(finalSessionId))
                .build();

        AssistantMessage response = agent.handle(message, config);
        String aiAnswer = response.getText();

        // 记录AI消息
        messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, modelName);

        AssistantResponse assistantResponse = new AssistantResponse();
        assistantResponse.setAnswer(aiAnswer);
        assistantResponse.setType(modelName);
        assistantResponse.setSuggestion("由 " + agent.getDescription() + " 为您服务");
        assistantResponse.setNeedsFurtherHelp(false);
        assistantResponse.setUserId(String.valueOf(userId));
        assistantResponse.setThreadId(threadId);
        assistantResponse.setSessionId(finalSessionId);

        return assistantResponse;
    }

    /**
     * 流式接口
     */
    @PostMapping(value = "/{agentType}/chat/stream", produces = "text/event-stream")
    public Flux<ServerSentEvent<String>> streamChat(@PathVariable String agentType, @RequestBody RequestDTO request) {
        Integer userId = Integer.parseInt(request.getUserId());
        String message = request.getMessage();
        Integer sessionId = request.getSessionId();

        BaseAgent agent = resolveAgent(agentType);
        String modelName = normalizeAgentType(agentType);
        Integer finalSessionId = ensureSession(userId, sessionId, modelName);

        // 记录用户消息
        messageService.createMessage(finalSessionId, userId, "user", message, modelName);

        RunnableConfig config = RunnableConfig.builder()
                .threadId(UUID.randomUUID().toString())
                .addMetadata("user_id", String.valueOf(userId))
                .addMetadata("session_id", String.valueOf(finalSessionId))
                .build();

        StringBuilder fullResponse = new StringBuilder();

        Flux<String> streamFlux;
        if (agent instanceof ScholarshipBailianAgent scholarship) {
            streamFlux = scholarship.streamHandle(message, config);
        } else if (agent instanceof CourseBailianAgent course) {
            streamFlux = course.streamHandle(message, config);
        } else if (agent instanceof TravelBailianAgent travel) {
            streamFlux = travel.streamHandle(message, config);
        } else if (agent instanceof ThesisBailianAgent thesis) {
            streamFlux = thesis.streamHandle(message, config);
        } else {
            // 不支持流式时，使用非流式转发
            AssistantMessage resp = agent.handle(message, config);
            String aiAnswer = resp.getText();
            messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, modelName);
            return Flux.just(ServerSentEvent.<String>builder().data(aiAnswer).build());
        }

        return streamFlux
                .doOnNext(chunk -> log.debug("【流式】{} 输出片段: {}", agent.getAgentName(), chunk))
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build())
                .doOnNext(sse -> {
                    if (sse.data() != null) {
                        fullResponse.append(sse.data());
                    }
                })
                .doOnComplete(() -> {
                    String aiAnswer = fullResponse.toString();
                    messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, modelName);
                    log.info("【流式】{} 响应完成，长度 {}", modelName, aiAnswer.length());
                })
                .doOnError(error -> log.error("【流式】{} 处理失败", agent.getAgentName(), error));
    }

    /**
     * 确保会话存在，创建或更新
     */
    private Integer ensureSession(Integer userId, Integer sessionId, String modelName) {
        if (sessionId == null) {
            return sessionService.createSession(userId, modelName).getId();
        }
        sessionService.updateSessionTime(sessionId);
        return sessionId;
    }

    /**
     * 根据路径参数解析到对应智能体
     */
    private BaseAgent resolveAgent(String agentType) {
        String normalized = normalizeAgentType(agentType);
        Map<String, BaseAgent> mapping = Map.of(
                "scholarship", scholarshipBailianAgent,
                "course", courseBailianAgent,
                "travel", travelBailianAgent,
                "thesis", thesisBailianAgent
        );
        BaseAgent agent = mapping.get(normalized);
        if (agent == null) {
            throw new IllegalArgumentException("不支持的智能体类型: " + agentType);
        }
        return agent;
    }

    /**
     * 统一归一化智能体类型
     */
    private String normalizeAgentType(String agentType) {
        return agentType.toLowerCase(Locale.ROOT);
    }
}
