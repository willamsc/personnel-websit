package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.dto.JwtAuthResponse;
import com.willamsc.personalwebsite.dto.LoginRequest;
import com.willamsc.personalwebsite.dto.RegisterRequest;
import com.willamsc.personalwebsite.entity.User;
import com.willamsc.personalwebsite.mapper.UserMapper;
import com.willamsc.personalwebsite.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务类
 * 处理用户注册、登录等认证相关业务
 * 
 * @author william
 * @since 2024-11-25
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数
     */
    public AuthService(AuthenticationManager authenticationManager,
                      JwtTokenProvider tokenProvider,
                      UserMapper userMapper,
                      PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return JWT认证响应
     */
    @Transactional
    public JwtAuthResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(registerRequest.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setNickname(registerRequest.getNickname());
        user.setRole("USER");
        user.setStatus(1);
        
        // 保存用户
        userMapper.insert(user);

        // 登录新用户
        return login(new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword()));
    }

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return JWT认证响应
     */
    public JwtAuthResponse login(LoginRequest loginRequest) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        // 设置认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌
        String jwt = tokenProvider.generateToken(authentication);

        // 获取用户信息
        User user = userMapper.findByUsername(loginRequest.getUsername());

        // 构建响应
        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 更新最后登录IP
     * 
     * @param username 用户名
     * @param request HTTP请求
     */
    public void updateLastLoginIp(String username, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        User user = userMapper.findByUsername(username);
        user.setLastLoginIp(ipAddress);
        userMapper.updateById(user);
    }
}
