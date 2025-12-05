package top.shy.campusassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置属性
 *
 * @author 15331
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {

    /**
     * OSS endpoint
     */
    private String endpoint;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 文件夹路径
     */
    private String folder;
}
