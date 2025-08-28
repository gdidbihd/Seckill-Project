package com.zj.seckill.rabbitmq;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.zj.seckill.config.RabbitMQSeckillConfig;
import com.zj.seckill.pojo.SeckillMessage;
import com.zj.seckill.pojo.User;
import com.zj.seckill.service.GoodsService;
import com.zj.seckill.service.OrderService;
import com.zj.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 消息消费者
@Service
@Slf4j
public class MQReceiverMessage {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    // 接收消息，并完成下单操作
    @RabbitListener(queues = RabbitMQSeckillConfig.QUEUE)
    public void receive(String message) {
        log.info("接收消息：" + message);

        // 将消息转换成SeckillMessage对象
        SeckillMessage seckillMessage = JSONUtil.toBean(message, SeckillMessage.class);
        // 获取用户
        User user = seckillMessage.getUser();
        // 获取商品id
        Long goodsId = seckillMessage.getGoodsId();
        // 获取秒杀商品对象
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        // 下单操作
        orderService.seckill(user, goodsVo);
    }
}
