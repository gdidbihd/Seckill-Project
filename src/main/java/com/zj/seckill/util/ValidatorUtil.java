package com.zj.seckill.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 校验工具类
 * 正则表达式校验
 */
public class ValidatorUtil {
    // 校验手机号码 （第一位必须是1，第二位是3-9，后面8位0-9范围内都可以，然后有一个结束符号）
    private static final Pattern mobile_pattern = Pattern.compile("^[1][3-9][0-9]{9}$");

    public static boolean isMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return false;
        }
        return mobile_pattern.matcher(mobile).matches();
    }

    @Test
    public void test() {
        System.out.println(isMobile("13300000000"));
    }

}
