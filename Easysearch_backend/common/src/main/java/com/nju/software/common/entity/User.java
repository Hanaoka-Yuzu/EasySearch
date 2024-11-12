package com.nju.software.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 用户DO 与db 对应
 * @Author wxy
 * @Date 2024/1/30
 **/
@Data
@TableName(value ="user_info")
public class User implements Serializable {
    /**
     * 用户uid
     */
    @TableId(type = IdType.AUTO)
    private int uid;

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
    private IdentityEnum identity;

    /**
     * 手机号
     */
    private String phone;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
