package com.willamsc.personalwebsite.integration;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 评论接口集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class CommentIntegrationTest extends BaseIntegrationTest {

    private String userToken;
    private HttpHeaders headers;
    private Long articleId;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 创建测试用户并登录
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("commentuser");
        registerRequest.setPassword("Test123!");
        registerRequest.setEmail("comment@example.com");
        registerRequest.setNickname("Comment User");

        restTemplate.exchange(
            createURLWithPort("/api/v1/auth/register"),
            HttpMethod.POST,
            new HttpEntity<>(registerRequest, headers),
            new ParameterizedTypeReference<Result<UserResponse>>() {}
        );

        // 登录获取token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUsername());
        loginRequest.setPassword(registerRequest.getPassword());

        ResponseEntity<Result<String>> loginResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/login"),
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, headers),
            new ParameterizedTypeReference<Result<String>>() {}
        );

        userToken = loginResponse.getBody().getData();
        headers.setBearerAuth(userToken);

        // 创建测试文章
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setTitle("Test Article for Comments");
        articleRequest.setContent("Test Content");
        articleRequest.setSummary("Test Summary");
        articleRequest.setCategoryId(1L);
        articleRequest.setTags("test");

        ResponseEntity<Result<ArticleResponse>> articleResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles"),
            HttpMethod.POST,
            new HttpEntity<>(articleRequest, headers),
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        articleId = articleResponse.getBody().getData().getId();
    }

    @Test
    void createAndGetComment() {
        // 创建评论
        CommentRequest request = new CommentRequest();
        request.setArticleId(articleId);
        request.setContent("Test Comment Content");

        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Result<CommentResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<CommentResponse>>() {}
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertTrue(createResponse.getBody().isSuccess());

        CommentResponse created = createResponse.getBody().getData();
        assertNotNull(created.getId());
        assertEquals(request.getContent(), created.getContent());
        assertEquals(articleId, created.getArticleId());

        // 获取评论
        ResponseEntity<Result<CommentResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments/" + created.getId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<CommentResponse>>() {}
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertTrue(getResponse.getBody().isSuccess());
        assertEquals(created.getId(), getResponse.getBody().getData().getId());
    }

    @Test
    void replyToComment() {
        // 先创建一个评论
        CommentRequest parentRequest = new CommentRequest();
        parentRequest.setArticleId(articleId);
        parentRequest.setContent("Parent Comment");

        ResponseEntity<Result<CommentResponse>> parentResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments"),
            HttpMethod.POST,
            new HttpEntity<>(parentRequest, headers),
            new ParameterizedTypeReference<Result<CommentResponse>>() {}
        );

        Long parentId = parentResponse.getBody().getData().getId();

        // 回复评论
        CommentRequest replyRequest = new CommentRequest();
        replyRequest.setArticleId(articleId);
        replyRequest.setContent("Reply Comment");
        replyRequest.setParentId(parentId);

        ResponseEntity<Result<CommentResponse>> replyResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments"),
            HttpMethod.POST,
            new HttpEntity<>(replyRequest, headers),
            new ParameterizedTypeReference<Result<CommentResponse>>() {}
        );

        assertEquals(HttpStatus.OK, replyResponse.getStatusCode());
        assertNotNull(replyResponse.getBody());
        assertTrue(replyResponse.getBody().isSuccess());
        assertEquals(parentId, replyResponse.getBody().getData().getParentId());
    }

    @Test
    void getArticleComments() {
        // 创建多个评论
        for (int i = 0; i < 3; i++) {
            CommentRequest request = new CommentRequest();
            request.setArticleId(articleId);
            request.setContent("Comment " + i);

            restTemplate.exchange(
                createURLWithPort("/api/v1/comments"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<Result<CommentResponse>>() {}
            );
        }

        // 获取文章评论列表
        ResponseEntity<Result<Page<CommentResponse>>> listResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments/article/" + articleId + "?page=0&size=10"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Page<CommentResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().isSuccess());
        assertTrue(listResponse.getBody().getData().getTotalElements() >= 3);
    }

    @Test
    void deleteComment() {
        // 创建评论
        CommentRequest request = new CommentRequest();
        request.setArticleId(articleId);
        request.setContent("Comment to delete");

        ResponseEntity<Result<CommentResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments"),
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<Result<CommentResponse>>() {}
        );

        Long commentId = createResponse.getBody().getData().getId();

        // 删除评论
        ResponseEntity<Result<Void>> deleteResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments/" + commentId),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Void>>() {}
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().isSuccess());

        // 验证评论已被删除
        ResponseEntity<Result<CommentResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/comments/" + commentId),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<CommentResponse>>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}
