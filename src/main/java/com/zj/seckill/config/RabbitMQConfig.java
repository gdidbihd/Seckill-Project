package com.zj.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // 定义队列名
    public static final String QUEUE = "queue";

    // 创建队列
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }
}
