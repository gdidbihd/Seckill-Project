package com.zj.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义防刷注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    // 时间范围
    int second();
    // 最大访问次数
    int maxCount();
    // 是否需要登录
    boolean needLogin() default true;
}
