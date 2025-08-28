package com.zj.seckill.config;

import com.zj.seckill.pojo.User;

public class UserContext {

    // 每个线程都有自己的线程本地变量，把共享数据放到这里，保证线程安全
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

    public static void remove() {
        userHolder.remove();
    }
}
