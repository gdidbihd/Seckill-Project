package com.zj.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zj.seckill.pojo.User;
import com.zj.seckill.service.UserService;
import com.zj.seckill.util.ValidatorUtil;
import com.zj.seckill.vo.LoginVo;
import com.zj.seckill.vo.RespBean;
import com.zj.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private UserService userService;

    @RequestMapping("/toLogin")
    public String toLogin() {
        // 跳到templates/login.html
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody // 返回json数据,而不是跳转页面
    // @Valid加入这个才会进行自动校验，也就是LoginVo属性上面的校验注解才会生效
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}", loginVo);
        return userService.doLogin(loginVo, request, response);
    }

}
