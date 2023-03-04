package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/4 15:20
 */
@Data
@ConfigurationProperties(prefix = "tanhua.huanxin")
public class HuanXinProperties {
    private String appkey;
    private String clientId;
    private String clientSecret;
}
