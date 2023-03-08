package com.tanhua.admin.job;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 14:37
 */
@Component
public class AnalysisTask {
    @Autowired
    private AnalysisService analysisService;

    /**
     * 定时统计日志数据到统计表中
     */
    @Scheduled(cron = "0/20 * * * * ? ")
    public void analysis() {
        // 业务逻辑
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("开始统计：" + time);
        analysisService.analysis();
        System.out.println("结束统计");
    }
}
