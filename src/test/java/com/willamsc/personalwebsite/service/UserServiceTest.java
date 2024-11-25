package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.BaseTest;
import com.willamsc.personalwebsite.dto.LoginRequest;
import com.willamsc.personalwebsite.dto.RegisterRequest;
import com.willamsc.personalwebsite.dto.UserResponse;
import com.willamsc.personalwebsite.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试类
 *
 * @author william
 * @since 2024-11-25
 */
class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("Test@123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setNickname("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Test@123");
    }

    @Test
    void register() {
        UserResponse response = userService.register(registerRequest);
        
        assertNotNull(response);
        assertEquals(registerRequest.getUsername(), response.getUsername());
        assertEquals(registerRequest.getEmail(), response.getEmail());
        assertEquals(registerRequest.getNickname(), response.getNickname());
    }

    @Test
    void registerWithExistingUsername() {
        // 先注册一个用户
        userService.register(registerRequest);
        
        // 使用相同用户名再次注册
        assertThrows(BusinessException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void registerWithInvalidPassword() {
        registerRequest.setPassword("weak");
        
        assertThrows(BusinessException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    void login() {
        // 先注册用户
        userService.register(registerRequest);
        
        // 登录
        String token = userService.login(loginRequest);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void loginWithWrongPassword() {
        // 先注册用户
        userService.register(registerRequest);
        
        // 使用错误密码登录
        loginRequest.setPassword("wrongpassword");
        
        assertThrows(BusinessException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void loginWithNonExistentUser() {
        loginRequest.setUsername("nonexistent");
        
        assertThrows(BusinessException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    void getUserInfo() {
        // 先注册用户
        UserResponse registered = userService.register(registerRequest);
        
        // 获取用户信息
        UserResponse userInfo = userService.getUserInfo(registered.getId());
        
        assertNotNull(userInfo);
        assertEquals(registered.getId(), userInfo.getId());
        assertEquals(registered.getUsername(), userInfo.getUsername());
        assertEquals(registered.getEmail(), userInfo.getEmail());
    }

    @Test
    void updateUserInfo() {
        // 先注册用户
        UserResponse registered = userService.register(registerRequest);
        
        // 更新用户信息
        RegisterRequest updateRequest = new RegisterRequest();
        updateRequest.setNickname("Updated Name");
        updateRequest.setEmail("updated@example.com");
        
        UserResponse updated = userService.updateUserInfo(registered.getId(), updateRequest);
        
        assertNotNull(updated);
        assertEquals("Updated Name", updated.getNickname());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void changePassword() {
        // 先注册用户
        UserResponse registered = userService.register(registerRequest);
        
        // 修改密码
        String newPassword = "NewTest@123";
        userService.changePassword(registered.getId(), registerRequest.getPassword(), newPassword);
        
        // 使用新密码登录
        loginRequest.setPassword(newPassword);
        String token = userService.login(loginRequest);
        
        assertNotNull(token);
    }

    @Test
    void changePasswordWithWrongOldPassword() {
        // 先注册用户
        UserResponse registered = userService.register(registerRequest);
        
        // 使用错误的旧密码修改密码
        String newPassword = "NewTest@123";
        
        assertThrows(BusinessException.class, () -> {
            userService.changePassword(registered.getId(), "wrongpassword", newPassword);
        });
    }
}
