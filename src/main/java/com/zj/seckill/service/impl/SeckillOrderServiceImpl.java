package com.zj.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.seckill.mapper.SeckillOrderMapper;
import com.zj.seckill.pojo.SeckillOrder;
import com.zj.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {
}
