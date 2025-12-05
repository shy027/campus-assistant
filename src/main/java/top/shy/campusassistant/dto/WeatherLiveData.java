package top.shy.campusassistant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 实时天气数据
 *
 * @author 15331
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherLiveData {
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 天气状况
     */
    private String weather;
    
    /**
     * 温度(摄氏度)
     */
    private String temperature;
    
    /**
     * 湿度
     */
    private String humidity;
    
    /**
     * 风向
     */
    @JsonProperty("winddirection")
    private String windDirection;
    
    /**
     * 风力等级
     */
    @JsonProperty("windpower")
    private String windPower;
    
    /**
     * 数据发布时间
     */
    @JsonProperty("reporttime")
    private String reportTime;
}
