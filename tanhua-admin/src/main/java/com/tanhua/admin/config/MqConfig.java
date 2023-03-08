package com.tanhua.admin.config;

import com.tanhua.commons.utils.MQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2023/3/8 11:40
 */
@Configuration
public class MqConfig {
    @Bean
    public TopicExchange logTopicExchange() {
        return new TopicExchange(MQConstants.LOG_EXCHANGE, true, false);
    }

    @Bean
    public Queue logQueue() {
        return new Queue(MQConstants.LOG_QUEUE, true);
    }

    @Bean
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(logTopicExchange()).with(MQConstants.LOG_ROUTING_KEY);
    }

    @Bean
    public TopicExchange auditTopicExchange() {
        return new TopicExchange(MQConstants.AUDIT_EXCHANGE, true, false);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue(MQConstants.AUDIT_QUEUE, true);
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder.bind(auditQueue()).to(auditTopicExchange()).with(MQConstants.AUDIT_ROUTING_KEY);
    }
}
