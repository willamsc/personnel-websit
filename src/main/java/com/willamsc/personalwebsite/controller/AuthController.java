package com.willamsc.personalwebsite.controller;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.JwtAuthResponse;
import com.willamsc.personalwebsite.dto.LoginRequest;
import com.willamsc.personalwebsite.dto.RegisterRequest;
import com.willamsc.personalwebsite.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理用户注册、登录等认证相关请求
 * 
 * @author william
 * @since 2024-11-25
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return Result.success("注册成功", authService.register(registerRequest));
    }

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<JwtAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                       HttpServletRequest request) {
        JwtAuthResponse response = authService.login(loginRequest);
        authService.updateLastLoginIp(loginRequest.getUsername(), request);
        return Result.success("登录成功", response);
    }
}
