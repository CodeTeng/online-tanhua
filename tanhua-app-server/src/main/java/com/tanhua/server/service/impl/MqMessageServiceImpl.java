package com.tanhua.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.MQConstants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.service.MqMessageService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 11:28
 */
@Service
public class MqMessageServiceImpl implements MqMessageService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void sendLogMessage(Long userId, String type, String key, String busId) {
        try {
            Map map = new HashMap();
            map.put("userId", userId.toString());
            map.put("type", type);
            map.put("logTime", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            map.put("busId", busId);
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend(MQConstants.LOG_EXCHANGE, "log." + key, message);
        } catch (AmqpException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorResult.builder().errMessage("发送消息失败").build());
        }
    }
}
