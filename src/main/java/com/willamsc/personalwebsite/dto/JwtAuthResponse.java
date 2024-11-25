package com.willamsc.personalwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT认证响应DTO
 * 包含访问令牌和用户基本信息
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    /**
     * JWT访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型，默认为"Bearer "
     */
    private String tokenType = "Bearer";

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户头像URL
     */
    private String avatar;
}
