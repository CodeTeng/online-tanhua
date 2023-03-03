package com.tanhua.server;

import com.tanhua.autoconfig.template.OssTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/1 22:36
 */
@SpringBootTest
public class OssTest {
    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void testTemplateUpload() throws FileNotFoundException {
        String path = "D:\\KaWaYiImage\\avatar.jpeg";
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        String url = ossTemplate.upload(path, fileInputStream);
        System.out.println(url);
    }
}
