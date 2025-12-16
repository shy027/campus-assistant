package top.shy.campusassistant.service.impl;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.shy.campusassistant.agent.BaseAgent;
import top.shy.campusassistant.agent.CourseBailianAgent;
import top.shy.campusassistant.agent.ScholarshipBailianAgent;
import top.shy.campusassistant.entity.Session;
import top.shy.campusassistant.model.AssistantResponse;
import top.shy.campusassistant.router.AgentRouter;
import top.shy.campusassistant.service.AgentService;
import top.shy.campusassistant.service.MessageService;
import top.shy.campusassistant.service.SessionService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Agent服务实现
 *
 * @author 15331
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRouter agentRouter;
    private final SessionService sessionService;
    private final MessageService messageService;

    // 存储用户的threadId映射
    private final Map<String, String> userThreadMap = new HashMap<>();

    @Override
    public AssistantResponse chat(Integer userId, String message, Integer sessionId) {
        // 路由到合适的Agent，提前确定模型/智能体
        BaseAgent agent = agentRouter.route(message);
        log.info("用户 {} 的消息被路由到 {}", userId, agent.getAgentName());

        // 处理会话
        Integer finalSessionId;
        if (sessionId == null) {
            Session session = sessionService.createSession(userId, agent.getAgentName());
            finalSessionId = session.getId();
            log.info("创建新会话，会话ID：{}，用户ID：{}", finalSessionId, userId);
        } else {
            sessionService.updateSessionTime(sessionId);
            finalSessionId = sessionId;
            log.info("使用现有会话，会话ID：{}", finalSessionId);
        }

        // 记录用户消息
        messageService.createMessage(finalSessionId, userId, "user", message, agent.getAgentName());

        // 为每个用户生成或获取 threadId
        String threadId = userThreadMap.computeIfAbsent(String.valueOf(userId), k -> UUID.randomUUID().toString());

        // 创建配置
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", String.valueOf(userId))
                .addMetadata("session_id", String.valueOf(finalSessionId))
                .build();

        // 调用Agent处理
        AssistantMessage response = agent.handle(message, config);
        String aiAnswer = response.getText();

        // 记录AI消息
        messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, agent.getAgentName());

        // 构建返回结果
        AssistantResponse assistantResponse = new AssistantResponse();
        assistantResponse.setAnswer(aiAnswer);
        assistantResponse.setType(agent.getAgentName());  // 使用Agent名称作为类型
        assistantResponse.setSuggestion("由 " + agent.getDescription() + " 为您服务");
        assistantResponse.setNeedsFurtherHelp(false);
        assistantResponse.setUserId(String.valueOf(userId));
        assistantResponse.setThreadId(threadId);
        assistantResponse.setSessionId(finalSessionId);

        return assistantResponse;
    }

    @Override
    public String getHistory(String userId) {
        String threadId = userThreadMap.get(userId);
        if (threadId == null) {
            return "未找到该用户的历史记录";
        }
        // 这里简化处理，实际应该实现完整的history接口
        return "用户ID: " + userId + ", ThreadID: " + threadId + " (历史记录功能需要进一步实现)";
    }

    @Override
    public Flux<ServerSentEvent<String>> streamChat(Integer userId, String message, Integer sessionId) {
        // 路由到合适的Agent，提前确定模型/智能体
        BaseAgent agent = agentRouter.route(message);
        log.info("【流式】用户 {} 的消息被路由到 {}", userId, agent.getAgentName());

        // 处理会话
        Integer finalSessionId;
        if (sessionId == null) {
            Session session = sessionService.createSession(userId, agent.getAgentName());
            finalSessionId = session.getId();
            log.info("【流式】创建新会话,会话ID:{},用户ID:{}", finalSessionId, userId);
        } else {
            sessionService.updateSessionTime(sessionId);
            finalSessionId = sessionId;
            log.info("【流式】使用现有会话ID:{}", finalSessionId);
        }

        // 记录用户消息
        messageService.createMessage(finalSessionId, userId, "user", message, agent.getAgentName());
        log.info("【流式】已记录用户消息,会话ID:{}", finalSessionId);

        // 为每个用户生成或获取 threadId
        String threadId = userThreadMap.computeIfAbsent(String.valueOf(userId), k -> UUID.randomUUID().toString());

        // 创建配置
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", String.valueOf(userId))
                .addMetadata("session_id", String.valueOf(finalSessionId))
                .build();

        // 检查Agent是否支持流式调用
        if (agent instanceof ScholarshipBailianAgent scholarshipAgent) {
            StringBuilder fullResponse = new StringBuilder();
            return scholarshipAgent.streamHandle(message, config)
                    .doOnNext(content -> log.debug("【流式】Scholarship输出片段: {}", content))
                    .map(content -> ServerSentEvent.<String>builder().data(content).build())
                    .doOnNext(sse -> {
                        if (sse.data() != null) {
                            fullResponse.append(sse.data());
                        }
                    })
                    .doOnComplete(() -> {
                        String aiAnswer = fullResponse.toString();
                        messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, agent.getAgentName());
                        log.info("【流式】Scholarship响应完成,记录消息长度:{}", aiAnswer.length());
                    })
                    .doOnError(error -> log.error("【流式】Scholarship处理失败", error));
        } else if (agent instanceof CourseBailianAgent courseAgent) {
            StringBuilder fullResponse = new StringBuilder();
            return courseAgent.streamHandle(message, config)
                    .doOnNext(content -> log.debug("【流式】Course输出片段: {}", content))
                    .map(content -> ServerSentEvent.<String>builder().data(content).build())
                    .doOnNext(sse -> {
                        if (sse.data() != null) {
                            fullResponse.append(sse.data());
                        }
                    })
                    .doOnComplete(() -> {
                        String aiAnswer = fullResponse.toString();
                        messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, agent.getAgentName());
                        log.info("【流式】Course响应完成,记录消息长度:{}", aiAnswer.length());
                    })
                    .doOnError(error -> log.error("【流式】Course处理失败", error));
        } else {
            // 其他Agent不支持流式，使用非流式方法并转换为Flux
            log.info("【流式】Agent {} 不支持流式，使用非流式模式", agent.getAgentName());
            AssistantMessage response = agent.handle(message, config);
            String aiAnswer = response.getText();

            // 记录AI消息
            messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, agent.getAgentName());
            log.info("【流式】非流式Agent响应完成,已记录消息");

            return Flux.just(ServerSentEvent.<String>builder().data(aiAnswer).build());
        }
    }
}
