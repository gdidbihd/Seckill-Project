package com.zj.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQSeckillConfig {
    // 定义队列名和交换机
    public static final String QUEUE = "seckillQueue";
    public static final String EXCHANGE = "seckillExchange";

    // 创建队列
    @Bean
    public Queue queue_seckill() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange topicExchange_seckill() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding_seckill() {
        return BindingBuilder.bind(queue_seckill()).to(topicExchange_seckill()).with("seckill.#");
    }
}
