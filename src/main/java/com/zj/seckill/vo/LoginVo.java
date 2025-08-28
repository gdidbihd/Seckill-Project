package com.zj.seckill.vo;

import com.zj.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 用户登录时，接受发送的信息 (mobile, password)
 */
@Data
public class LoginVo {
    // 对属性值进行约束
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;

}
