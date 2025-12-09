package top.shy.campusassistant.service.impl;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Service;
import top.shy.campusassistant.agent.BaseAgent;
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

    // 存储用户的 threadId 映射
    private final Map<String, String> userThreadMap = new HashMap<>();

    private static final String MODEL_NAME = "qwen-max";

    @Override
    public AssistantResponse chat(Integer userId, String message, Integer sessionId) {
        // 处理会话
        Integer finalSessionId;
        if (sessionId == null) {
            // 创建新会话
            Session session = sessionService.createSession(userId);
            finalSessionId = session.getId();
            log.info("创建新会话，会话ID：{}，用户ID：{}", finalSessionId, userId);
        } else {
            // 更新现有会话的更新时间
            sessionService.updateSessionTime(sessionId);
            finalSessionId = sessionId;
            log.info("使用现有会话，会话ID：{}", finalSessionId);
        }

        // 记录用户消息
        messageService.createMessage(finalSessionId, userId, "user", message, MODEL_NAME);

        // 为每个用户生成或获取 threadId
        String threadId = userThreadMap.computeIfAbsent(String.valueOf(userId), k -> UUID.randomUUID().toString());

        // 创建配置
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", String.valueOf(userId))
                .addMetadata("session_id", String.valueOf(finalSessionId))
                .build();

        // 路由到合适的Agent
        BaseAgent agent = agentRouter.route(message);
        log.info("用户 {} 的消息被路由到: {}", userId, agent.getAgentName());

        // 调用Agent处理
        AssistantMessage response = agent.handle(message, config);
        String aiAnswer = response.getText();

        // 记录AI消息
        messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, MODEL_NAME);

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
        // 这里简化处理,实际应该实现完整的history接口
        return "用户ID: " + userId + ", ThreadID: " + threadId + " (历史记录功能需要进一步实现)";
    }

    @Override
    public reactor.core.publisher.Flux<org.springframework.http.codec.ServerSentEvent<String>> streamChat(Integer userId, String message, Integer sessionId) {
        // 处理会话
        Integer finalSessionId;
        if (sessionId == null) {
            // 创建新会话
            Session session = sessionService.createSession(userId);
            finalSessionId = session.getId();
            log.info("【流式】创建新会话,会话ID:{},用户ID:{}", finalSessionId, userId);
        } else {
            // 更新现有会话的更新时间
            sessionService.updateSessionTime(sessionId);
            finalSessionId = sessionId;
            log.info("【流式】使用现有会话,会话ID:{}", finalSessionId);
        }

        // 记录用户消息
        messageService.createMessage(finalSessionId, userId, "user", message, MODEL_NAME);
        log.info("【流式】已记录用户消息,会话ID:{}", finalSessionId);

        // 为每个用户生成或获取 threadId
        String threadId = userThreadMap.computeIfAbsent(String.valueOf(userId), k -> UUID.randomUUID().toString());

        // 创建配置
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", String.valueOf(userId))
                .addMetadata("session_id", String.valueOf(finalSessionId))
                .build();

        // 路由到合适的Agent
        BaseAgent agent = agentRouter.route(message);
        log.info("【流式】用户 {} 的消息被路由到: {}", userId, agent.getAgentName());

        // 检查Agent是否支持流式调用
        if (agent instanceof top.shy.campusassistant.agent.ScholarshipBailianAgent) {
            top.shy.campusassistant.agent.ScholarshipBailianAgent bailianAgent = 
                (top.shy.campusassistant.agent.ScholarshipBailianAgent) agent;
            
            // 使用StringBuilder收集完整响应用于记录
            StringBuilder fullResponse = new StringBuilder();
            
            return bailianAgent.streamHandle(message, config)
                .doOnNext(content -> {
                    log.debug("【流式】ScholarshipAgent输出片段: {}", content);
                })
                .map(content -> org.springframework.http.codec.ServerSentEvent.<String>builder()
                    .data(content)
                    .build())
                .doOnNext(sse -> {
                    if (sse.data() != null) {
                        fullResponse.append(sse.data());
                        log.debug("【流式】发送SSE数据: {}", sse.data());
                    }
                })
                .doOnComplete(() -> {
                    // 流式完成后记录完整的AI消息
                    String aiAnswer = fullResponse.toString();
                    messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, MODEL_NAME);
                    log.info("【流式】ScholarshipAgent响应完成,已记录消息,总长度:{}", aiAnswer.length());
                })
                .doOnError(error -> {
                    log.error("【流式】ScholarshipAgent处理失败", error);
                });
        } else if (agent instanceof top.shy.campusassistant.agent.CourseBailianAgent) {
            top.shy.campusassistant.agent.CourseBailianAgent bailianAgent = 
                (top.shy.campusassistant.agent.CourseBailianAgent) agent;
            
            // 使用StringBuilder收集完整响应用于记录
            StringBuilder fullResponse = new StringBuilder();
            
            return bailianAgent.streamHandle(message, config)
                .doOnNext(content -> {
                    log.debug("【流式】CourseAgent输出片段: {}", content);
                })
                .map(content -> org.springframework.http.codec.ServerSentEvent.<String>builder()
                    .data(content)
                    .build())
                .doOnNext(sse -> {
                    if (sse.data() != null) {
                        fullResponse.append(sse.data());
                        log.debug("【流式】发送SSE数据: {}", sse.data());
                    }
                })
                .doOnComplete(() -> {
                    // 流式完成后记录完整的AI消息
                    String aiAnswer = fullResponse.toString();
                    messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, MODEL_NAME);
                    log.info("【流式】CourseAgent响应完成,已记录消息,总长度:{}", aiAnswer.length());
                })
                .doOnError(error -> {
                    log.error("【流式】CourseAgent处理失败", error);
                });
        } else {
            // 其他Agent不支持流式,使用非流式方法并转换为Flux
            log.info("【流式】Agent {} 不支持流式,使用非流式模式", agent.getAgentName());
            AssistantMessage response = agent.handle(message, config);
            String aiAnswer = response.getText();
            
            // 记录AI消息
            messageService.createMessage(finalSessionId, userId, "ai", aiAnswer, MODEL_NAME);
            log.info("【流式】非流式Agent响应完成,已记录消息");

            return reactor.core.publisher.Flux.just(
                org.springframework.http.codec.ServerSentEvent.<String>builder()
                    .data(aiAnswer)
                    .build()
            );
        }
    }
}
