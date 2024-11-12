package com.nju.software.user.vo;

import lombok.Data;

/**
 * @Description TODO 登录使用
 * @Author wxy
 * @Date 2024/1/30
 **/
@Data
public class UserVO {
    /**
     * 用户名
     */
    private String name;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户身份
     */
    private String identity;

    /**
     * 手机号
     */
    private String phone;
}
