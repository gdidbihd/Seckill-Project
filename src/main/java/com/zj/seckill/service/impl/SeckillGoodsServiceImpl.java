package com.zj.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.seckill.mapper.SeckillGoodsMapper;
import com.zj.seckill.pojo.SeckillGoods;
import com.zj.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {
}
