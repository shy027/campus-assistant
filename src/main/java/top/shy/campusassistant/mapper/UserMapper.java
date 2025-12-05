package top.shy.campusassistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.shy.campusassistant.entity.User;

/**
 * 用户 Mapper
 *
 * @author mqxu
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);
}
