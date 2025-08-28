package com.zj.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

/**
 * @author zj
 * @create 2019-03-09 17:05
 * MD5工具类,两次加盐加密
 */

public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String SALT = "k4kIqlOw";

    // 加密加盐
    public static String inputPassToMidPass(String inputPass) {
        String str = SALT.charAt(6) + inputPass + SALT.charAt(4);
        return md5(str);
    }

    // 加密加盐 md5（ md5（pass+salt1）+salt2）
    public static String midPassToDBPass(String midPass, String salt) {
        String str = salt.charAt(5) + midPass + salt.charAt(2);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt) {
        String midPass = inputPassToMidPass(inputPass);
        String dbPass = midPassToDBPass(midPass, salt);
        return dbPass;
    }

    @Test
    public void testMD5() {
        System.out.println(MD5Util.md5("123456"));
        System.out.println(MD5Util.inputPassToMidPass("123456"));
        System.out.println(MD5Util.midPassToDBPass("27d9071a52726dc322b14a5afd85bc97","Gn3iOamf"));
        System.out.println(MD5Util.inputPassToDBPass("123","Gn3iOamf"));
    }
}
