package com.willamsc.personalwebsite.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.willamsc.personalwebsite.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * 继承自BaseEntity，包含用户的基本信息和状态
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /**
     * 用户名，唯一标识
     */
    private String username;

    /**
     * 密码，使用BCrypt加密存储
     */
    private String password;

    /**
     * 邮箱地址，用于通知和找回密码
     */
    private String email;

    /**
     * 用户昵称，显示名称
     */
    private String nickname;

    /**
     * 用户角色
     * ADMIN: 管理员
     * USER: 普通用户
     */
    private String role;

    /**
     * 用户状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 最后登录IP地址
     */
    private String lastLoginIp;
}
