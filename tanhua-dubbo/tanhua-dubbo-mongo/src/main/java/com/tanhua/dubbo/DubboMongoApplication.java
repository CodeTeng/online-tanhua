package com.tanhua.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/2 15:05
 */
@SpringBootApplication
@EnableAsync
public class DubboMongoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboMongoApplication.class, args);
    }
}
