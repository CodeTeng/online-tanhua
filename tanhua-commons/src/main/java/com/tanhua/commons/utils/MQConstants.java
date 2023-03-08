package com.tanhua.commons.utils;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 11:33
 */
public interface MQConstants {
    String LOG_QUEUE = "tanhua.log.queue";
    String LOG_EXCHANGE = "tanhua.log.exchange";
    String LOG_ROUTING_KEY = "log.*";
    String AUDIT_EXCHANGE = "tanhua.audit.exchange";
}
