package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 22:51
 */
@Data
@ConfigurationProperties(prefix = "tanhua.aip")
public class AipFaceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;
}
