package com.zj.seckill.rabbitmq;

import com.zj.seckill.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息生产者
 */
@Service
@Slf4j
public class MQSender {
    // 装配RabbitMQ模板
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Object msg) {
        // 发送消息
        log.info("发送消息:" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE, msg);
    }
}
