package com.tanhua.admin.listener;

import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 21:54
 */
@Component
public class MovementListener {
    @DubboReference
    private MovementApi movementApi;
    @Autowired
    private AliyunGreenTemplate aliyunGreenTemplate;

    @RabbitListener(queues = "tanhua.audit.queue")
    public void listenCreate(String movementId) {
        try {
            // 1、根据动态id查询动态
            Movement movement = movementApi.findById(movementId);
            // 对于RabbitMQ消息有可能出现重复，解决方法判断 (幂等性)
            Integer state = 0;
            if (movement != null && movement.getState() == 0) {
                Map<String, String> textScan = aliyunGreenTemplate.greenTextScan(movement.getTextContent());
                Map<String, String> imageScan = aliyunGreenTemplate.imageScan(movement.getMedias());
                if (textScan != null && imageScan != null) {
                    String textSuggestion = textScan.get("suggestion");
                    String imageSuggestion = imageScan.get("suggestion");
                    if ("block".equals(textSuggestion)) {
                        state = 2;
                    } else if ("pass".equals(textSuggestion)) {
                        state = 1;
                    }
                }
            }
            movementApi.update(movementId, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
