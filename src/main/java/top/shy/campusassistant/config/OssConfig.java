package top.shy.campusassistant.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 *
 * @author 15331
 */
@Configuration
@RequiredArgsConstructor
public class OssConfig {

    private final OssProperties ossProperties;

    /**
     * 创建OSS客户端
     */
    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }
}
