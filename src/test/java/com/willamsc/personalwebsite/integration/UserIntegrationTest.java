package com.willamsc.personalwebsite.integration;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.LoginRequest;
import com.willamsc.personalwebsite.dto.RegisterRequest;
import com.willamsc.personalwebsite.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户接口集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class UserIntegrationTest extends BaseIntegrationTest {

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void registerAndLogin() {
        // 注册新用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("Test123!");
        registerRequest.setEmail("test@example.com");
        registerRequest.setNickname("Test User");

        HttpEntity<RegisterRequest> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<Result<UserResponse>> registerResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/register"),
            HttpMethod.POST,
            registerEntity,
            new ParameterizedTypeReference<Result<UserResponse>>() {}
        );

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertTrue(registerResponse.getBody().isSuccess());
        assertEquals(registerRequest.getUsername(), registerResponse.getBody().getData().getUsername());

        // 登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(registerRequest.getPassword());

        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Result<String>> loginResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/login"),
            HttpMethod.POST,
            loginEntity,
            new ParameterizedTypeReference<Result<String>>() {}
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertTrue(loginResponse.getBody().isSuccess());
        assertNotNull(loginResponse.getBody().getData()); // JWT token
    }

    @Test
    void loginWithInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("wrongpassword");

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Result<String>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/login"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<String>>() {}
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getUserProfile() {
        // 先登录获取token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        ResponseEntity<Result<String>> loginResponse = restTemplate.postForEntity(
            createURLWithPort("/api/v1/auth/login"),
            loginRequest,
            new ParameterizedTypeReference<Result<String>>() {}
        );

        String token = loginResponse.getBody().getData();
        headers.setBearerAuth(token);

        // 获取用户信息
        ResponseEntity<Result<UserResponse>> profileResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/users/profile"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<UserResponse>>() {}
        );

        assertEquals(HttpStatus.OK, profileResponse.getStatusCode());
        assertNotNull(profileResponse.getBody());
        assertTrue(profileResponse.getBody().isSuccess());
        assertEquals("admin", profileResponse.getBody().getData().getUsername());
    }

    @Test
    void updateUserProfile() {
        // 先登录获取token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        ResponseEntity<Result<String>> loginResponse = restTemplate.postForEntity(
            createURLWithPort("/api/v1/auth/login"),
            loginRequest,
            new ParameterizedTypeReference<Result<String>>() {}
        );

        String token = loginResponse.getBody().getData();
        headers.setBearerAuth(token);

        // 更新用户信息
        RegisterRequest updateRequest = new RegisterRequest();
        updateRequest.setNickname("Updated Admin");
        updateRequest.setEmail("updated@example.com");

        HttpEntity<RegisterRequest> updateEntity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Result<UserResponse>> updateResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/users/profile"),
            HttpMethod.PUT,
            updateEntity,
            new ParameterizedTypeReference<Result<UserResponse>>() {}
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertTrue(updateResponse.getBody().isSuccess());
        assertEquals(updateRequest.getNickname(), updateResponse.getBody().getData().getNickname());
        assertEquals(updateRequest.getEmail(), updateResponse.getBody().getData().getEmail());
    }
}
