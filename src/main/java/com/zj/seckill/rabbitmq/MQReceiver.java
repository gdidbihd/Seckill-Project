package com.zj.seckill.rabbitmq;

import com.zj.seckill.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    // 接收消息
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String msg) {
        log.info("接收到消息->>" + msg);
    }
}
