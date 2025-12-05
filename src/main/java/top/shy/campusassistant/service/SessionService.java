package top.shy.campusassistant.service;

import top.shy.campusassistant.dto.SessionResponse;
import top.shy.campusassistant.entity.Session;

import java.util.List;

/**
 * 会话服务接口
 *
 * @author 15331
 */
public interface SessionService {

    /**
     * 创建会话
     *
     * @param userId 用户ID
     * @return 会话信息
     */
    Session createSession(Integer userId);

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<SessionResponse> getSessionsByUserId(Integer userId);

    /**
     * 根据ID查询会话
     *
     * @param id 会话ID
     * @return 会话信息
     */
    SessionResponse getSessionById(Integer id);

    /**
     * 更新会话标题
     *
     * @param id 会话ID
     * @param title 新标题
     * @return 更新后的会话信息
     */
    SessionResponse updateSessionTitle(Integer id, String title);

    /**
     * 删除会话（逻辑删除）
     *
     * @param id 会话ID
     */
    void deleteSession(Integer id);

    /**
     * 更新会话的更新时间
     *
     * @param id 会话ID
     */
    void updateSessionTime(Integer id);
}
