package top.shy.campusassistant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 高德地图天气API响应
 *
 * @author 15331
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AMapWeatherResponse {
    
    /**
     * 返回状态 1:成功 0:失败
     */
    private String status;
    
    /**
     * 返回信息
     */
    private String info;
    
    /**
     * 返回状态码
     */
    private String infocode;
    
    /**
     * 实时天气数据列表
     */
    private List<WeatherLiveData> lives;
    
    /**
     * 天气预报数据列表
     */
    private List<WeatherForecastData> forecasts;
}
