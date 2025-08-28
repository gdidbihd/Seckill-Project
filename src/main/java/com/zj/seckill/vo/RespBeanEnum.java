package com.zj.seckill.vo;

import com.sun.deploy.net.DownloadException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),

    // 登录
    LOGIN_ERROR(500210, "用户id或密码错误"),
    MOBILE_ERROR(500211, "手机号码格式错误"),
    BING_ERROR(500212, "参数绑定异常"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_ERROR(500214, "密码更新失败"),

    // 秒杀模块返回信息
    ENTRY_STOCK(500550, "库存不足"),
    REPEAT_ERROR(500551, "每人限购一件，不能重复抢购"),

    REQUEST_ILLEGAL(500502, "请求非法"),
    SESSION_ERROR(500503, "用户信息有误"),
    SEC_KILL_WAIT(500504, "秒杀排队中..."),
    CAPTCHA_ERROR(500505, "验证码错误"),
    ACCESS_LIMIT_ERROR(500506, "访问太频繁，请稍后再试"),
    SEC_KILL_RETRY(500507, "秒杀失败，请重试");

    private final Integer code;
    private final String message;
}
