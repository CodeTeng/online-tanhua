package com.tanhua.server;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/6 20:10
 */
@SpringBootTest
public class FastDFSTest {
    /**
     * 从调度服务器获取，一个目标存储服务器，上传
     */
    @Autowired
    private FastFileStorageClient client;

    /**
     * 获取存储服务器的请求URL
     */
    @Autowired
    private FdfsWebServer webServer;

    @Test
    public void testUploadFile() throws Exception {
        // 1. 指定文件
        File file = new File("D:\\KaWaYiImage\\demo.jpg");
        // 2. 文件上传
        StorePath storePath = client.uploadFile(new FileInputStream(file), file.length(), "jpg", null);
        String fullPath = storePath.getFullPath();
        // group1/M00/00/00/wKhQgWQF2cWAfqmcAACAiriQ_MY917.jpg
        System.out.println(fullPath);
        // 3. 拼接url
        // http://192.168.80.129:8888/group1/M00/00/00/wKhQgWQF2cWAfqmcAACAiriQ_MY917.jpg
        String url = webServer.getWebServerUrl() + fullPath;
        System.out.println(url);
    }
}
