package top.shy.campusassistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.shy.campusassistant.entity.Session;

import java.util.List;

/**
 * 会话 Mapper
 *
 * @author 15331
 */
@Mapper
public interface SessionMapper extends BaseMapper<Session> {

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Session> selectByUserId(Integer userId);
}
