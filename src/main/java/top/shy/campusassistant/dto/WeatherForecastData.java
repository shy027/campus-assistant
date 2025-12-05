package top.shy.campusassistant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 天气预报数据
 *
 * @author 15331
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastData {
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 预报数据列表
     */
    private List<Cast> casts;
    
    /**
     * 每日预报数据
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cast {
        /**
         * 日期
         */
        private String date;
        
        /**
         * 星期
         */
        private String week;
        
        /**
         * 白天天气
         */
        @JsonProperty("dayweather")
        private String dayWeather;
        
        /**
         * 夜间天气
         */
        @JsonProperty("nightweather")
        private String nightWeather;
        
        /**
         * 白天温度
         */
        @JsonProperty("daytemp")
        private String dayTemp;
        
        /**
         * 夜间温度
         */
        @JsonProperty("nighttemp")
        private String nightTemp;
        
        /**
         * 白天风向
         */
        @JsonProperty("daywind")
        private String dayWind;
        
        /**
         * 夜间风向
         */
        @JsonProperty("nightwind")
        private String nightWind;
        
        /**
         * 白天风力
         */
        @JsonProperty("daypower")
        private String dayPower;
        
        /**
         * 夜间风力
         */
        @JsonProperty("nightpower")
        private String nightPower;
    }
}
