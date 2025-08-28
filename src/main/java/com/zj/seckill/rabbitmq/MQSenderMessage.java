package com.zj.seckill.rabbitmq;

import com.baomidou.mybatisplus.extension.api.R;
import com.zj.seckill.config.RabbitMQSeckillConfig;
import com.zj.seckill.pojo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 消息生产者
@Service
@Slf4j
public class MQSenderMessage {

    // 装配template
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSeckillMessage(String message) {
        log.info("发送消息:" + message);
        rabbitTemplate.convertAndSend(RabbitMQSeckillConfig.EXCHANGE,"seckill.message", message);
    }
}
