package com.tanhua.server;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 23:04
 */
@SpringBootTest
public class AipFaceTemplateTest {
    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Test
    public void detectFace() {
        String image = "https://tanhua0823.oss-cn-shanghai.aliyuncs.com/2023/03/01/860add6d-c5d7-4bdf-aec4-b04baff2975c.jpeg";
        boolean detect = aipFaceTemplate.detect(image);
        System.out.println(detect);
    }
}
