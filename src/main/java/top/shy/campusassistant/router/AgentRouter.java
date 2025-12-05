package top.shy.campusassistant.router;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.shy.campusassistant.agent.BaseAgent;
import top.shy.campusassistant.agent.CampusInfoAgent;
import top.shy.campusassistant.agent.CourseBailianAgent;
import top.shy.campusassistant.agent.LibraryAgent;
import top.shy.campusassistant.agent.ScholarshipBailianAgent;
import top.shy.campusassistant.agent.WeatherAgent;

import java.util.Arrays;
import java.util.List;

/**
 * Agent路由器
 * 根据用户消息选择合适的Agent处理
 *
 * @author 15331
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentRouter {

    private final ScholarshipBailianAgent scholarshipBailianAgent;
    private final CourseBailianAgent courseBailianAgent;
    private final WeatherAgent weatherAgent;
    private final LibraryAgent libraryAgent;
    private final CampusInfoAgent campusInfoAgent;

    /**
     * 路由用户消息到合适的Agent
     *
     * @param message 用户消息
     * @return 选中的Agent
     */
    public BaseAgent route(String message) {
        // 按优先级尝试匹配Agent
        List<BaseAgent> agents = Arrays.asList(
                scholarshipBailianAgent,
                courseBailianAgent,
                weatherAgent,
                libraryAgent,
                campusInfoAgent  // 默认Agent,放在最后
        );

        for (BaseAgent agent : agents) {
            if (agent.canHandle(message)) {
                log.info("路由消息到: {} - 消息: {}", agent.getAgentName(), message);
                return agent;
            }
        }

        // 如果没有匹配到,默认使用CampusInfoAgent
        log.info("使用默认Agent: {} - 消息: {}", campusInfoAgent.getAgentName(), message);
        return campusInfoAgent;
    }

    /**
     * 获取所有可用的Agent
     *
     * @return Agent列表
     */
    public List<BaseAgent> getAllAgents() {
        return Arrays.asList(scholarshipBailianAgent, courseBailianAgent, weatherAgent, libraryAgent, campusInfoAgent);
    }
}
