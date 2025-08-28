package com.zj.seckill.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/*
 * 生成UUID的工具类
 */
public class UUIDUtil {
    public static String uuid() {
        // 默认生成 xxxx-yyyy-zzzz-dddd
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Test
    public void test() {
        System.out.println(uuid());
    }
}
