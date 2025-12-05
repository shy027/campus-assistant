package top.shy.campusassistant.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.shy.campusassistant.dto.AMapWeatherResponse;
import top.shy.campusassistant.dto.WeatherForecastData;
import top.shy.campusassistant.dto.WeatherLiveData;
import top.shy.campusassistant.service.AMapWeatherService;

/**
 * 高德地图天气服务实现
 *
 * @author 15331
 */
@Slf4j
@Service
public class AMapWeatherServiceImpl implements AMapWeatherService {
    
    private static final String AMAP_WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    
    @Value("${amap.api-key}")
    private String apiKey;
    
    @Value("${amap.weather.city-code}")
    private String cityCode;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public AMapWeatherServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public WeatherLiveData getLiveWeather() {
        try {
            log.info("开始获取实时天气,城市编码: {}", cityCode);
            
            // 构建请求URL
            String url = String.format("%s?key=%s&city=%s&extensions=base", 
                AMAP_WEATHER_URL, apiKey, cityCode);
            
            // 调用API
            String response = restTemplate.getForObject(url, String.class);
            log.debug("高德地图API响应: {}", response);
            
            // 解析响应
            AMapWeatherResponse weatherResponse = objectMapper.readValue(response, AMapWeatherResponse.class);
            
            // 检查响应状态
            if (!"1".equals(weatherResponse.getStatus())) {
                log.error("获取天气失败: {}", weatherResponse.getInfo());
                throw new RuntimeException("获取天气失败: " + weatherResponse.getInfo());
            }
            
            // 返回实时天气数据
            if (weatherResponse.getLives() != null && !weatherResponse.getLives().isEmpty()) {
                WeatherLiveData liveData = weatherResponse.getLives().get(0);
                log.info("获取实时天气成功: {} {}°C {}", liveData.getCity(), 
                    liveData.getTemperature(), liveData.getWeather());
                return liveData;
            } else {
                log.error("实时天气数据为空");
                throw new RuntimeException("实时天气数据为空");
            }
            
        } catch (Exception e) {
            log.error("获取实时天气异常", e);
            throw new RuntimeException("获取实时天气失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public WeatherForecastData getForecastWeather() {
        try {
            log.info("开始获取天气预报,城市编码: {}", cityCode);
            
            // 构建请求URL
            String url = String.format("%s?key=%s&city=%s&extensions=all", 
                AMAP_WEATHER_URL, apiKey, cityCode);
            
            // 调用API
            String response = restTemplate.getForObject(url, String.class);
            log.debug("高德地图API响应: {}", response);
            
            // 解析响应
            AMapWeatherResponse weatherResponse = objectMapper.readValue(response, AMapWeatherResponse.class);
            
            // 检查响应状态
            if (!"1".equals(weatherResponse.getStatus())) {
                log.error("获取天气预报失败: {}", weatherResponse.getInfo());
                throw new RuntimeException("获取天气预报失败: " + weatherResponse.getInfo());
            }
            
            // 返回天气预报数据
            if (weatherResponse.getForecasts() != null && !weatherResponse.getForecasts().isEmpty()) {
                WeatherForecastData forecastData = weatherResponse.getForecasts().get(0);
                log.info("获取天气预报成功: {} 未来{}天", forecastData.getCity(), 
                    forecastData.getCasts() != null ? forecastData.getCasts().size() : 0);
                return forecastData;
            } else {
                log.error("天气预报数据为空");
                throw new RuntimeException("天气预报数据为空");
            }
            
        } catch (Exception e) {
            log.error("获取天气预报异常", e);
            throw new RuntimeException("获取天气预报失败: " + e.getMessage(), e);
        }
    }
}
