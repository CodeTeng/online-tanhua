package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 22:34
 */
@ConfigurationProperties(prefix = "tanhua.oss")
@Data
public class OssProperties {
    private String accessKey;
    private String secret;
    private String bucketName;
    private String url;
    private String endpoint;
}
