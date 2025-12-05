package top.shy.campusassistant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.shy.campusassistant.dto.SessionResponse;
import top.shy.campusassistant.entity.Session;
import top.shy.campusassistant.mapper.SessionMapper;
import top.shy.campusassistant.service.SessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话服务实现
 *
 * @author 15331
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionMapper sessionMapper;

    private static final String DEFAULT_TITLE = "未命名会话";
    private static final String DEFAULT_MODEL = "qwen-max";

    @Override
    public Session createSession(Integer userId) {
        Session session = new Session();
        session.setTitle(DEFAULT_TITLE);
        session.setUserId(userId);
        session.setModelName(DEFAULT_MODEL);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        session.setDeleted(0);

        sessionMapper.insert(session);
        log.info("创建会话成功，会话ID：{}，用户ID：{}", session.getId(), userId);
        return session;
    }

    @Override
    public List<SessionResponse> getSessionsByUserId(Integer userId) {
        List<Session> sessions = sessionMapper.selectByUserId(userId);
        return sessions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SessionResponse getSessionById(Integer id) {
        Session session = sessionMapper.selectById(id);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        return convertToResponse(session);
    }

    @Override
    public SessionResponse updateSessionTitle(Integer id, String title) {
        Session session = sessionMapper.selectById(id);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        Session updateSession = new Session();
        updateSession.setId(id);
        updateSession.setTitle(title);
        updateSession.setUpdateTime(LocalDateTime.now());

        sessionMapper.updateById(updateSession);
        log.info("更新会话标题成功，会话ID：{}，新标题：{}", id, title);

        return getSessionById(id);
    }

    @Override
    public void deleteSession(Integer id) {
        Session session = sessionMapper.selectById(id);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        sessionMapper.deleteById(id);
        log.info("删除会话成功，会话ID：{}", id);
    }

    @Override
    public void updateSessionTime(Integer id) {
        Session updateSession = new Session();
        updateSession.setId(id);
        updateSession.setUpdateTime(LocalDateTime.now());
        sessionMapper.updateById(updateSession);
    }

    /**
     * 转换为响应DTO
     */
    private SessionResponse convertToResponse(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .userId(session.getUserId())
                .modelName(session.getModelName())
                .createTime(session.getCreateTime())
                .updateTime(session.getUpdateTime())
                .build();
    }
}
