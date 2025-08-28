package com.zj.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.seckill.pojo.Goods;
import com.zj.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface GoodsService extends IService<Goods> {
    // 秒杀商品列表
    List<GoodsVo> findGoodsVo();

    // 获取商品详情，根据goodsId
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
