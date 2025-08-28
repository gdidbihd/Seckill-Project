package com.zj.seckill.controller;

import com.zj.seckill.rabbitmq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RabbitMQHandler {

    @Autowired
    private MQSender mqSender;

    // 调用生产者发送消息
    @RequestMapping("/mq")
    @ResponseBody
    public void sendMQ() {
        mqSender.send("Hello, zj~~~");
    }
}
