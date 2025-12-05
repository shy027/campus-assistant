package top.shy.campusassistant.common.cache;

/**
 * Redis Key 管理
 *
 * @author mqxu
 */
public class RedisKeys {

    /**
     * 短信验证码 Key
     */
    public static String getSmsCodeKey(String mobile) {
        return "sms:code:" + mobile;
    }

    /**
     * 用户 Token Key
     */
    public static String getUserTokenKey(Long userId) {
        return "user:token:" + userId;
    }
}
