package top.shy.campusassistant.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.shy.campusassistant.tool.CampusInfoTool;
import top.shy.campusassistant.tool.CourseQueryTool;
import top.shy.campusassistant.tool.LibrarySeatTool;
import top.shy.campusassistant.tool.WeatherTool;

/**
 * Agent配置类
 * 配置DashScope和各个Tool
 *
 * @author 15331
 */
@Configuration
public class AgentConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public DashScopeChatModel chatModel(DashScopeApi dashScopeApi) {
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();
    }

    @Bean
    public ToolCallback campusInfoToolCallback(CampusInfoTool campusInfoTool) {
        return FunctionToolCallback
                .builder("getCampusInfo", campusInfoTool)
                .description("查询校园相关信息，如图书馆、食堂、教务处等")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback weatherToolToolCallback(WeatherTool weatherTool) {
        return FunctionToolCallback
                .builder("getWeather", weatherTool)
                .description("查询南京天气信息。参数queryType: 'live'(实时天气) 或 'forecast'(天气预报)")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback courseQueryToolCallback(CourseQueryTool courseQueryTool) {
        return FunctionToolCallback
                .builder("getCourseInfo", courseQueryTool)
                .description("查询课程信息，如课程名称、时间、地点")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback librarySeatToolCallback(LibrarySeatTool librarySeatTool) {
        return FunctionToolCallback
                .builder("getLibrarySeat", librarySeatTool)
                .description("查询图书馆座位情况，如空闲座位数、位置等")
                .inputType(String.class)
                .build();
    }
}
