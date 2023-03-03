package com.tanhua.autoconfig.template;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.autoconfig.properties.OssProperties;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 22:35
 */
public class OssTemplate {
    private OssProperties ossProperties;

    public OssTemplate(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    public String upload(String filename, InputStream is) {
        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getAccessKey();
        String accessKeySecret = ossProperties.getSecret();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String storePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "/" + UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
        System.out.println(storePath);
        ossClient.putObject(ossProperties.getBucketName(), storePath, is);
        String url = ossProperties.getUrl() + storePath;
        // 关闭OSSClient。
        ossClient.shutdown();
        return url;
    }
}
