package com.zj.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zj.seckill.pojo.User;
import com.zj.seckill.service.UserService;
import com.zj.seckill.util.CookieUtil;
import com.zj.seckill.vo.RespBean;
import com.zj.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

// 自定义拦截器，
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 把user放入threadlocal中
        // 限流防刷注解生效

        if(handler instanceof HandlerMethod){
            // 获取user对象
            User user = getUser(request, response);
            //放入当前线程中：用户当前的web直接获得user使用
            UserContext.setUser(user);

            // 处理注解
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                // 如果目标方法没有这个注解，直接放行
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            if(needLogin){
                // 需要登录，但是没有登录，返回了，没有通过拦截器不准往下走了
                if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
            }
            String key = "access:" + request.getRequestURI() + ":" + user.getId();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if(count == null){
                // 说明该用户没有访问过，或者访问过，但是访问的次数小于5
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            }else if(count< maxCount){
                valueOperations.increment(key);
            }else{
                render(response, RespBeanEnum.ACCESS_LIMIT_ERROR);
                return false;
            }
        }

        return true;
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        return userService.getUserByCookie(userTicket, request, response);
    }

    // 构建返回对象，以流的方式
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        // 构建返回对象
        RespBean error = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除当前线程中的参数
        UserContext.remove();
    }
}
