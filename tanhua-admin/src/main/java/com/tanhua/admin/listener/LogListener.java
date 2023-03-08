package com.tanhua.admin.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.admin.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 11:40
 */
@Component
public class LogListener {
    @Autowired
    private LogMapper logMapper;

    @RabbitListener(queues = "tanhua.log.queue")
    public void createListen(String message) {
        try {
            Map map = JSON.parseObject(message);
            // 1、获取数据
            Long userId = Long.valueOf(map.get("userId").toString());
            String logTime = (String) map.get("logTime");
            String type = (String) map.get("type");
            // 2、保存到数据库
            Log log = new Log(userId, logTime, type);
            logMapper.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
