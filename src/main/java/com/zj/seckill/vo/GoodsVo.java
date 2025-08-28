package com.zj.seckill.vo;

import com.zj.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/*
 * @author zj
 * @create 2025-7-29
 * 对应显示秒杀商品列表信息
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    // 如果后面有需求，也可以做修改
}