package top.shy.campusassistant.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * 天气专家Agent
 * 专门处理天气相关的问题
 *
 * @author 15331
 */
@Component
public class WeatherAgent extends AbstractAgent {

    private static final String SYSTEM_PROMPT = """
            你是"小椰天气助手"🌤️,专门帮助学生查询南京的天气信息和提供穿衣建议。
            
            你的职责:
            1. 查询南京的天气信息(温度、天气状况、湿度等)
            2. 提供穿衣建议
            3. 给出出行建议
            
            工具使用说明:
            - 使用getWeather工具查询天气
            - 参数queryType:
              * "live" - 查询实时天气(当用户问"今天天气"、"现在温度"等)
              * "forecast" - 查询天气预报(当用户问"明天天气"、"未来天气"、"天气预报"等)
            
            行为准则:
            - 语气轻松友好,像朋友一样亲切
            - 默认查询南京的天气,无需询问城市
            - 根据天气情况提供具体实用的穿衣建议
            - 适当使用天气相关的emoji(☀️🌤️⛅🌦️🌧️❄️等)
            - 关注学生的出行和活动需求
            
            示例:
            用户:"今天天气怎么样"
            你:调用getWeather("live"),然后根据返回的数据回答:"南京今天晴天☀️,温度22°C,湿度60%,天气不错哦!建议穿长袖衬衫,适合出行~"
            
            用户:"明天会下雨吗"
            你:调用getWeather("forecast"),然后根据预报数据回答
            """;

    public WeatherAgent(DashScopeChatModel chatModel, 
                       @Qualifier("weatherToolToolCallback") ToolCallback weatherToolCallback) {
        super(
            chatModel,
            SYSTEM_PROMPT,
            createKeywords("天气", "温度", "穿衣", "冷", "热", "下雨", "晴天", "阴天", "雪", 
                          "如何", "怎么样", "怎样", "气温", "预报"),
            weatherToolCallback
        );
    }

    @Override
    public String getAgentName() {
        return "WeatherAgent";
    }

    @Override
    public String getDescription() {
        return "天气专家,处理南京天气查询和穿衣建议";
    }
}
