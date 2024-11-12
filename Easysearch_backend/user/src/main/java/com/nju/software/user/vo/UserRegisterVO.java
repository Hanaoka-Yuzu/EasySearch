package com.nju.software.user.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册使用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterVO {

    @NotEmpty(message = "用户名不能为空")
    @Pattern(regexp = "[A-Za-z0-9_]{3,15}", message = "用户名只能由大写或者小写字母，数字或者下划线组成，且长度在3~15之间")
    private String name;

    @NotEmpty(message = "密码不能为空")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "(13\\d|14[579]|15[^4\\D]|17[^49\\D]|18\\d)\\d{8}", message = "手机号格式错误")
    private String phone;

    private String identity;

    @NotEmpty(message = "验证码不能为空")
    private String code;
}

