package com.zj.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.seckill.pojo.Order;
import com.zj.seckill.pojo.User;
import com.zj.seckill.vo.GoodsVo;

public interface OrderService extends IService<Order> {
    // 秒杀方法
    Order seckill(User user, GoodsVo goods);

    // 生成秒杀路径（唯一）
    String createPath(User user, Long goodsId);

    // 校验秒杀路径
    boolean checkPath(User user, Long goodsId, String path);

    // 验证用户输入的验证码是否正确
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
