package top.shy.campusassistant.service;

import top.shy.campusassistant.entity.User;

/**
 * 用户服务接口
 *
 * @author mqxu
 */
public interface UserService {

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息，不存在返回 null
     */
    User getByPhone(String phone);

    /**
     * 创建新用户（首次登录注册）
     *
     * @param phone    手机号
     * @param password 密码（可为空）
     * @return 新创建的用户
     */
    User createUser(String phone, String password);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    void updateLastLoginTime(Integer userId);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getById(Integer userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param username 用户名（可为空）
     * @param password 密码（可为空）
     * @param email 邮箱（可为空）
     * @return 更新后的用户信息
     */
    User updateUserInfo(Integer userId, String username, String password, String email);

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 更新后的用户信息
     */
    User updateAvatar(Integer userId, String avatarUrl);
}
