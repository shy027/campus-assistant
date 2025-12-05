package top.shy.campusassistant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.shy.campusassistant.common.result.Result;
import top.shy.campusassistant.dto.*;
import top.shy.campusassistant.service.MessageService;
import top.shy.campusassistant.service.SessionService;

import java.util.List;

/**
 * 会话管理控制器
 *
 * @author 15331
 */
@Tag(name = "会话管理", description = "AI对话会话管理相关接口")
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SessionController {

    private final SessionService sessionService;
    private final MessageService messageService;

    @Operation(summary = "创建会话", description = "创建一个新的AI对话会话")
    @PostMapping
    public Result<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        var session = sessionService.createSession(request.getUserId());
        SessionResponse response = SessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .userId(session.getUserId())
                .modelName(session.getModelName())
                .createTime(session.getCreateTime())
                .updateTime(session.getUpdateTime())
                .build();
        return Result.ok(response);
    }

    @Operation(summary = "查询会话列表", description = "根据用户ID查询所有会话")
    @GetMapping
    public Result<List<SessionResponse>> getSessionsByUserId(@RequestParam Integer userId) {
        List<SessionResponse> sessions = sessionService.getSessionsByUserId(userId);
        return Result.ok(sessions);
    }

    @Operation(summary = "查询会话详情", description = "根据会话ID查询会话详细信息")
    @GetMapping("/{id}")
    public Result<SessionResponse> getSessionById(@PathVariable Integer id) {
        SessionResponse session = sessionService.getSessionById(id);
        return Result.ok(session);
    }

    @Operation(summary = "修改会话标题", description = "修改指定会话的标题")
    @PutMapping("/{id}/title")
    public Result<SessionResponse> updateSessionTitle(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSessionTitleRequest request) {
        SessionResponse session = sessionService.updateSessionTitle(id, request.getTitle());
        return Result.ok(session);
    }

    @Operation(summary = "删除会话", description = "删除指定会话（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSession(@PathVariable Integer id) {
        sessionService.deleteSession(id);
        return Result.ok();
    }

    @Operation(summary = "查询会话消息历史", description = "查询指定会话的所有消息记录")
    @GetMapping("/{id}/messages")
    public Result<List<MessageResponse>> getSessionMessages(@PathVariable Integer id) {
        List<MessageResponse> messages = messageService.getMessagesBySessionId(id);
        return Result.ok(messages);
    }
}
