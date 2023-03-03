package com.tanhua.autoconfig.template;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.tanhua.autoconfig.properties.SmsProperties;

/**
 * @description: 短信模板
 * @author: ~Teng~
 * @date: 2023/3/1 20:10
 */
public class SmsTemplate {
    private SmsProperties smsProperties;

    public SmsTemplate(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    public void sendSms(String mobile, String code) {
        // 配置阿里云
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(smsProperties.getAccessKey())
                // 您的AccessKey Secret
                .setAccessKeySecret(smsProperties.getSecret())
                .setEndpoint("dysmsapi.aliyuncs.com");
        try {
            Client client = new Client(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(mobile)
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            System.out.println(response.getBody().toString());
            System.out.println(response.getBody().message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
