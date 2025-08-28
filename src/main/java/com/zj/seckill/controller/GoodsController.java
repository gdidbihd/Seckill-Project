package com.zj.seckill.controller;

import com.zj.seckill.pojo.User;
import com.zj.seckill.service.GoodsService;
import com.zj.seckill.service.UserService;
import com.zj.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 手动进行渲染的模板解析器
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    //  @CookieValue(value = "userTicket")直接拿到cookie中的名字为userTicket的值
    /*@RequestMapping("/toList")
    *//*public String toList(HttpSession session, Model model,
                         @CookieValue(value = "userTicket") String ticket) {*//*
    public String toList(Model model,
                         @CookieValue(value = "userTicket") String ticket,
                         HttpServletRequest request, HttpServletResponse response) {
        // 如果cookie没有生成，就重新登陆
        if(!StringUtils.hasText(ticket)){
            return "login";
        }
        // 通过ticket获取session中存放的user
        *//*User user = (User) session.getAttribute(ticket);*//*

        // 通过ticket获取redis中存放的user
        User user = userService.getUserByCookie(ticket, request, response);

        if(null == user){
            return "login";
        }
        // 将user保存到model中,给下一个模板使用
        model.addAttribute("user", user);

        return "goodsList";
    }*/

    @RequestMapping("/toListDB")
    // 根据后面三个参数直接转成user对象后，放到参数里面
    public String toListDB(Model model, User user) {

        if(null == user){
            return "login";
        }
        // 将user保存到model中,给下一个模板使用
        model.addAttribute("user", user);

        // 将商品列表信息放入model,给下一个模板使用
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        return "goodsList";
    }

    // 使用redis进行优化
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    // 根据后面三个参数直接转成user对象后，放到参数里面
    public String toList(Model model, User user,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        if(null == user){
            return "login";
        }

        // 先到Redis中获取缓存,如果有，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(StringUtils.hasText(html)){
            return html;
        }

        // 将user保存到model中,给下一个模板使用
        model.addAttribute("user", user);

        // 将商品列表信息放入model,给下一个模板使用
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        // 如果从Redis没有获取到页面，将静态页面拿过来手动渲染，并缓存
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);

        if(StringUtils.hasText(html)){
            // 将页面保存到Redis中，并设置过期时间，60s
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }

        return html;
    }

    // 进入商品详情页，根据商品id
    /*@RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId) {
        if(user == null){
            return "login";
        }

        // 将user放入model中
        model.addAttribute("user", user);
        // 通过goodsId，获取指定商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        // 返回秒杀状态和秒杀倒计时
        // secKillStatus秒杀状态，0秒杀未开始，1秒杀进行中，2秒杀已结束
        // remainSeconds秒杀的剩余时间 >0:表示还有多久开始秒杀，0：秒杀进行中，-1：秒杀已结束
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            secKillStatus = 1;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);

        return "goodsDetail";
    }*/

    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request, HttpServletResponse response) {
        if(user == null){
            return "login";
        }

        // 先到Redis中获取缓存,如果有，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:"+goodsId);
        if(StringUtils.hasText(html)){
            return html;
        }

        // 将user放入model中
        model.addAttribute("user", user);
        // 通过goodsId，获取指定商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        // 返回秒杀状态和秒杀倒计时
        // secKillStatus秒杀状态，0秒杀未开始，1秒杀进行中，2秒杀已结束
        // remainSeconds秒杀的剩余时间 >0:表示还有多久开始秒杀，0：秒杀进行中，-1：秒杀已结束
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            secKillStatus = 1;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);

        // 如果从Redis没有获取到页面，将静态页面拿过来手动渲染，并缓存
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);

        if(StringUtils.hasText(html)){
            // 将页面保存到Redis中，并设置过期时间，60s
            valueOperations.set("goodsDetail:"+goodsId, html, 60, TimeUnit.SECONDS);
        }

        return html;
    }
}
