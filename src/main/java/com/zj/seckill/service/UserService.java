package com.zj.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zj.seckill.pojo.User;
import com.zj.seckill.vo.LoginVo;
import com.zj.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {
    // 完成用户登录校验
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    // 根据cookie-ticket获取用户
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    // 更新密码
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
