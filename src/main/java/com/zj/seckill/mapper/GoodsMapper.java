package com.zj.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zj.seckill.pojo.Goods;
import com.zj.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {
    // 这两个方法都是与秒杀商品表联合查询，
    // goods_stock是普通商品库存
    // stock_count才是秒杀商品库存

    // 获取商品列表-秒杀
    List<GoodsVo> findGoodsVo();

    // 获取指定商品详情-根据id
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}