package com.zj.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import com.zj.seckill.config.AccessLimit;
import com.zj.seckill.pojo.Order;
import com.zj.seckill.pojo.SeckillMessage;
import com.zj.seckill.pojo.SeckillOrder;
import com.zj.seckill.pojo.User;
import com.zj.seckill.rabbitmq.MQSenderMessage;
import com.zj.seckill.service.GoodsService;
import com.zj.seckill.service.OrderService;
import com.zj.seckill.service.SeckillOrderService;
import com.zj.seckill.vo.GoodsVo;
import com.zj.seckill.vo.RespBean;
import com.zj.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 利用一个map记录是否还有库存
    // 内存标记
    private Map<Long, Boolean> entryStockMap = new HashMap<>();

    @Autowired
    private MQSenderMessage mqSenderMessage;

    // 处理用户抢购请求
    /*@RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        System.out.println("-------------秒杀V1.0--------------");

        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        // 判断库存
        if(goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 判断用户是否在复购，判断是否用户id和商品id在秒杀表中
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 进行抢购
        Order order = orderService.seckill(user, goodsVo);

        if(order== null){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        // 进入订单页
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);

        System.out.println("-------------秒杀V1.0--------------");

        return "orderDetail"; // 跳转到订单详情页面
    }*/

    /*@RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        log.info("-------------秒杀V3.0--------------");

        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);


        // 判断用户是否在复购，直接去redis中查询是否存在秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            // 说明该用户已经抢购该商品
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 首先判断库存，没有库存一切都白说
        if(entryStockMap.get(goodsId)){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 库存预减，减少去数据库中的请求，防止线程堆积
        // decrement具有原子性，
        Long decrement= redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if(decrement < 0){
            // 说明没有库存了，map设置为true
            entryStockMap.put(goodsId, true);
            // 说明没有足够的库存让用户去数据库中购买了，直接打道回府
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }
        // 先判断有没有复购以及商品过后在进行数据库查询
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        // 进行抢购
        Order order = orderService.seckill(user, goodsVo);

        if(order== null){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        // 进入订单页
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);

        log.info("-------------秒杀V3.0--------------");

        return "orderDetail"; // 跳转到订单详情页面
    }*/

    // 加入消息队列，完成秒杀异步请求
    /*@RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        // log.info("-------------秒杀V5.0--------------");

        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);


        // 判断用户是否在复购，直接去redis中查询是否存在秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            // 说明该用户已经抢购该商品
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 首先判断库存，没有库存一切都白说
        if(entryStockMap.get(goodsId)){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 库存预减，减少去数据库中的请求，防止线程堆积
        // decrement具有原子性，
        Long decrement= redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if(decrement < 0){
            // 说明没有库存了，map设置为true
            entryStockMap.put(goodsId, true);
            // 说明没有足够的库存让用户去数据库中购买了，直接打道回府
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 抢购，向消息队列发送秒杀请求，实现秒杀异步请求
        //发送消息后，快速返回一个临时结果（排队中）
        // 客户可以轮询获取最终结果
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
        model.addAttribute("errmsg", "秒杀排队中...");
        log.info("-------------秒杀V5.0--------------");
        return "secKillFail";
    }*/

    @Autowired
    private RedisScript<Long> script;
    // 引入分布式锁，扩大隔离性范围，这样就可以保证多个操作是线程安全的
    @RequestMapping("/doSeckill/distributed")
    public String doSeckill(Model model, User user, Long goodsId) {

        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);


        // 判断用户是否在复购，直接去redis中查询是否存在秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            // 说明该用户已经抢购该商品
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        // 首先判断库存，没有库存一切都白说
        if(entryStockMap.get(goodsId)){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            // 错误之后到错误页面
            return "secKillFail";
        }

        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        //2 获取锁成功
        if (lock) {
            //写自己的业务-就可以有多个操作了
            Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
            if (decrement < 0) {//说明这个商品已经没有库存
                //说明当前秒杀的商品，已经没有库存
                entryStockMap.put(goodsId, true);
                //恢复库存为0
                redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
                //释放锁.-lua为什么使用redis+lua脚本释放锁前面讲过
                redisTemplate.execute(script, Arrays.asList("lock"), uuid);
                model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
                return "secKillFail";//错误页面
            }
            //释放分布式锁
            redisTemplate.execute(script, Arrays.asList("lock"), uuid);

        } else {
            //3 获取锁失败,返回个信息[本次抢购失败，请再次抢购...]
            model.addAttribute("errmsg", RespBeanEnum.SEC_KILL_RETRY.getMessage());
            return "secKillFail";//错误页面
        }

        // 抢购，向消息队列发送秒杀请求，实现秒杀异步请求
        //发送消息后，快速返回一个临时结果（排队中）
        // 客户可以轮询获取最终结果
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
        model.addAttribute("errmsg", "秒杀排队中...");
        log.info("-------------Redis分布式锁示例--------------");
        return "secKillFail";
    }

    // 加入秒杀安全，直接返回RespBean
    @RequestMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, Model model, User user, Long goodsId) {

        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 验证path
        if(!orderService.checkPath(user, goodsId, path)){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        // 判断用户是否在复购，直接去redis中查询是否存在秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            // 说明该用户已经抢购该商品
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }

        // 首先判断库存，没有库存一切都白说
        if(entryStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        // 库存预减，减少去数据库中的请求，防止线程堆积
        // decrement具有原子性，
        Long decrement= redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if(decrement < 0){
            // 说明没有库存了，map设置为true
            entryStockMap.put(goodsId, true);
            // 说明没有足够的库存让用户去数据库中购买了，直接打道回府
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        // 抢购，向消息队列发送秒杀请求，实现秒杀异步请求
        //发送消息后，快速返回一个临时结果（排队中）
        // 客户可以轮询获取最终结果
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
        log.info("-------------秒杀V6.0--------------");
        return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
    }

    // public class SeckillController implements InitializingBean
    // 有一个初始化方法，秒杀系统启动的时候执行
    @Override
    public void afterPropertiesSet() throws Exception {
        // 该方法是在类的属性初始化之后执行，即在spring容器启动的时候执行，用于初始化缓存
        // 可以将库存量先装载到Redis中去
        List<GoodsVo> list = goodsService.findGoodsVo();
        // 先判断是否有秒杀商品
        if(CollectionUtils.isEmpty(list)){
            return;
        }

        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            // false表示有库存， true表示无库存
            entryStockMap.put(goodsVo.getId(), false);
        });
    }

    // 获取秒杀路径
    @RequestMapping("/path")
    @ResponseBody
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if(user == null || goodsId < 0 || !StringUtils.hasText(captcha)){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        /*// 加入Redis计数器，完成限流防刷
        String uri = request.getRequestURI();
        String key = uri + ":" + user.getId();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer count = (Integer) valueOperations.get(key);
        if(count == null){
            // 说明该用户没有访问过，或者访问过，但是访问的次数小于5
            valueOperations.set(key, 1, 5, TimeUnit.SECONDS);
        }else if(count<5){
            valueOperations.increment(key);
        }else{
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_ERROR);
        }*/

        // 校验用户输入的验证码
        if(!orderService.checkCaptcha(user, goodsId, captcha)){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        // 获取秒杀路径
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    // 生成验证码
    @RequestMapping("/captcha")
    public void happyCaptcha(HttpServletRequest request, HttpServletResponse response, User user, Long goodsId) {
        //生成验证码，并输出
        //注意，该验证码，默认就保存到session中, key是 happy-captcha
        HappyCaptcha.require(request, response)
                .style(CaptchaStyle.ANIM)               //设置展现样式为动画
                .type(CaptchaType.NUMBER)               //设置验证码内容为数字
                .length(6)                              //设置字符长度为6
                .width(220)                             //设置动画宽度为220
                .height(80)                             //设置动画高度为80
                .font(Fonts.getInstance().zhFont())     //设置汉字的字体
                .build().finish();                      //生成并输出验证码

        //把验证码的值，保存Redis [考虑项目分布式], 设置了验证码的失效时间100s
        //key: captcha:userId:goodsId
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId
                , (String) request.getSession().getAttribute("happy-captcha"), 100, TimeUnit.SECONDS);
    }
}
