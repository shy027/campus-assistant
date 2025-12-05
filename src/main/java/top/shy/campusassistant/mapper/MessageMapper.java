package top.shy.campusassistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.shy.campusassistant.entity.Message;

import java.util.List;

/**
 * 消息 Mapper
 *
 * @author 15331
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据会话ID查询消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<Message> selectBySessionId(Integer sessionId);
}
