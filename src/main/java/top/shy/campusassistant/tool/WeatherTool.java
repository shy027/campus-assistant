package top.shy.campusassistant.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import top.shy.campusassistant.dto.WeatherForecastData;
import top.shy.campusassistant.dto.WeatherLiveData;
import top.shy.campusassistant.service.AMapWeatherService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 天气查询工具
 * 集成高德地图天气API
 *
 * @author 15331
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherTool implements BiFunction<String, ToolContext, String> {
    
    private final AMapWeatherService aMapWeatherService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String apply(
            @ToolParam(description = "查询类型: live(实时天气) 或 forecast(天气预报)") String queryType,
            ToolContext toolContext) {
        
        try {
            log.info("天气查询工具被调用,查询类型: {}", queryType);
            
            // 根据查询类型调用不同的服务
            if ("forecast".equalsIgnoreCase(queryType)) {
                // 查询天气预报
                WeatherForecastData forecastData = aMapWeatherService.getForecastWeather();
                return formatForecastData(forecastData);
            } else {
                // 默认查询实时天气
                WeatherLiveData liveData = aMapWeatherService.getLiveWeather();
                return formatLiveData(liveData);
            }
            
        } catch (Exception e) {
            log.error("天气查询失败", e);
            return String.format("{ \"error\": \"天气查询失败: %s\" }", e.getMessage());
        }
    }
    
    /**
     * 格式化实时天气数据
     */
    private String formatLiveData(WeatherLiveData data) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("type", "live");
            result.put("city", data.getCity());
            result.put("province", data.getProvince());
            result.put("weather", data.getWeather());
            result.put("temperature", data.getTemperature() + "°C");
            result.put("humidity", data.getHumidity() + "%");
            result.put("windDirection", data.getWindDirection());
            result.put("windPower", data.getWindPower() + "级");
            result.put("reportTime", data.getReportTime());
            
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("格式化实时天气数据失败", e);
            return "{}";
        }
    }
    
    /**
     * 格式化天气预报数据
     */
    private String formatForecastData(WeatherForecastData data) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("type", "forecast");
            result.put("city", data.getCity());
            result.put("province", data.getProvince());
            
            // 格式化预报数据
            if (data.getCasts() != null && !data.getCasts().isEmpty()) {
                result.put("forecasts", data.getCasts());
            }
            
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("格式化天气预报数据失败", e);
            return "{}";
        }
    }
}
