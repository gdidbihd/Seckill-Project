package com.zj.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.seckill.exception.GlobalException;
import com.zj.seckill.mapper.UserMapper;
import com.zj.seckill.pojo.User;
import com.zj.seckill.service.UserService;
import com.zj.seckill.util.CookieUtil;
import com.zj.seckill.util.MD5Util;
import com.zj.seckill.util.UUIDUtil;
import com.zj.seckill.util.ValidatorUtil;
import com.zj.seckill.vo.LoginVo;
import com.zj.seckill.vo.RespBean;
import com.zj.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        // 接收mobile和password[midPass]
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        /*// 判断手机号/id 和密码是否为空
        if(!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        // 判断手机号码是否合格
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/

        // 查询DB，看用户是否存在
        User user = userMapper.selectById(mobile);
        if(null == user){
           // return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST);
            // 抛出全局异常后会自动被异常处理器处理
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }

        // 比对密码
        if(!MD5Util.midPassToDBPass(password, user.getSlat()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        // 用户登录成功后，给每个用户生成唯一的ticket
        String ticket = UUIDUtil.uuid();
        // 登录成功的用户保存到session
        /*request.getSession().setAttribute(ticket, user);*/

        // 把用户登录相关的存放到Redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);

        // 将ticket保存到cookie中
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(!StringUtils.hasText(userTicket)){
            return null;
        }

        // 根据票据去Redis获取用户
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        // 如果用户不为空，就刷新cookie,重新计时，防止过期
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(user == null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }

        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int i = userMapper.updateById(user);
        if(i == 1){
            // 删除redis中的数据
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }

        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_ERROR);
    }
}
