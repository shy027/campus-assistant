package top.shy.campusassistant.service;

import top.shy.campusassistant.dto.WeatherForecastData;
import top.shy.campusassistant.dto.WeatherLiveData;

/**
 * 高德地图天气服务接口
 *
 * @author 15331
 */
public interface AMapWeatherService {
    
    /**
     * 获取实时天气
     *
     * @return 实时天气数据
     */
    WeatherLiveData getLiveWeather();
    
    /**
     * 获取天气预报
     *
     * @return 天气预报数据
     */
    WeatherForecastData getForecastWeather();
}
