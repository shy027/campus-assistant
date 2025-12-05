package top.shy.campusassistant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.shy.campusassistant.dto.MessageResponse;
import top.shy.campusassistant.entity.Message;
import top.shy.campusassistant.mapper.MessageMapper;
import top.shy.campusassistant.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现
 *
 * @author 15331
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public Message createMessage(Integer sessionId, Integer userId, String role, String content, String modelName) {
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setModelName(modelName);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setDeleted(0);

        messageMapper.insert(message);
        log.info("创建消息成功，消息ID：{}，会话ID：{}，角色：{}", message.getId(), sessionId, role);
        return message;
    }

    @Override
    public List<MessageResponse> getMessagesBySessionId(Integer sessionId) {
        List<Message> messages = messageMapper.selectBySessionId(sessionId);
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应DTO
     */
    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .sessionId(message.getSessionId())
                .userId(message.getUserId())
                .role(message.getRole())
                .content(message.getContent())
                .modelName(message.getModelName())
                .createTime(message.getCreateTime())
                .build();
    }
}
