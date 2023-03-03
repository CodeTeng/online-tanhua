package com.tanhua.server;

import com.tanhua.autoconfig.template.SmsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmsTemplateTest {
    @Autowired
    private SmsTemplate smsTemplate;

    @Test
    public void testSendSms() {
        smsTemplate.sendSms("15290654216", "4567");
    }
}
