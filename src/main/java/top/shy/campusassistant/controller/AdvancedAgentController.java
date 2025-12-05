package top.shy.campusassistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import io.swagger.v3.oas.annotations.tags.Tag;
import top.shy.campusassistant.model.AssistantResponse;
import top.shy.campusassistant.model.RequestDTO;
import top.shy.campusassistant.service.AgentService;

import java.util.HashMap;
import java.util.Map;

/**
 * 高级Agent控制器
 * 使用多Agent架构处理用户请求
 *
 * @author 15331
 */
@Tag(name = "AI管理", description = "AI聊天、记录相关接口")
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdvancedAgentController {

    private final AgentService agentService;

    @PostMapping("/chat")
    public AssistantResponse chat(@RequestBody RequestDTO request) {
        String userId = request.getUserId();
        String message = request.getMessage();
        Integer sessionId = request.getSessionId();
        
        // 使用AgentService处理请求,内部会自动路由到合适的Agent
        // 并处理会话创建/更新和消息记录
        return agentService.chat(Integer.parseInt(userId), message, sessionId);
    }

    @PostMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamChat(@RequestBody RequestDTO request) {
        String userId = request.getUserId();
        String message = request.getMessage();
        Integer sessionId = request.getSessionId();
        
        // 使用AgentService处理流式请求,内部会自动路由到合适的Agent
        // 并处理会话创建/更新和消息记录
        return agentService.streamChat(Integer.parseInt(userId), message, sessionId);
    }

    @GetMapping("/history/{userId}")
    public Map<String, Object> getHistory(@PathVariable String userId) {
        String history = agentService.getHistory(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("history", history);
        return result;
    }
}
