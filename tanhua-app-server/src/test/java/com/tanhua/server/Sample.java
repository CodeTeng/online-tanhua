package com.tanhua.server;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;

public class Sample {
    public static void main(String[] args_) throws Exception {
        String accessKeyId = "LTAI4GAX9FhwjEhVjxJb9ugL";
        String accessKeySecret = "dgpqJiegJgcZyZuQ5ul7tIoweFFY8B";

        // 配置阿里云
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new com.aliyun.dysmsapi20170525.Client(config);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers("15290654216")
                .setSignName("黑马旅游网")
                .setTemplateCode("SMS_205877727")
                .setTemplateParam("{\"code\":\"1234\"}");
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse response = client.sendSms(sendSmsRequest);
        SendSmsResponseBody body = response.getBody();
        System.out.println(body);
    }
}