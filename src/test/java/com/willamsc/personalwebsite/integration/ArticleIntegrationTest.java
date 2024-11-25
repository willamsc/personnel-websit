package com.willamsc.personalwebsite.integration;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章接口集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class ArticleIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        // 登录获取token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        ResponseEntity<Result<String>> loginResponse = restTemplate.postForEntity(
            createURLWithPort("/api/v1/auth/login"),
            loginRequest,
            new ParameterizedTypeReference<Result<String>>() {}
        );

        adminToken = loginResponse.getBody().getData();

        // 设置请求头
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
    }

    @Test
    void createAndGetArticle() {
        // 创建文章
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Integration Test Article");
        request.setContent("Integration Test Content");
        request.setSummary("Integration Test Summary");
        request.setCategoryId(1L);
        request.setTags("test,integration");

        HttpEntity<ArticleRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Result<ArticleResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertTrue(createResponse.getBody().isSuccess());

        ArticleResponse created = createResponse.getBody().getData();
        assertNotNull(created.getId());
        assertEquals(request.getTitle(), created.getTitle());

        // 获取文章
        ResponseEntity<Result<ArticleResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles/" + created.getId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertTrue(getResponse.getBody().isSuccess());
        assertEquals(created.getId(), getResponse.getBody().getData().getId());
    }

    @Test
    void updateArticle() {
        // 先创建文章
        ArticleRequest createRequest = new ArticleRequest();
        createRequest.setTitle("Original Title");
        createRequest.setContent("Original Content");
        createRequest.setSummary("Original Summary");
        createRequest.setCategoryId(1L);
        createRequest.setTags("test");

        HttpEntity<ArticleRequest> createEntity = new HttpEntity<>(createRequest, headers);

        ResponseEntity<Result<ArticleResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles"),
            HttpMethod.POST,
            createEntity,
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        ArticleResponse created = createResponse.getBody().getData();

        // 更新文章
        ArticleRequest updateRequest = new ArticleRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");
        updateRequest.setSummary("Updated Summary");
        updateRequest.setCategoryId(1L);
        updateRequest.setTags("test,updated");

        HttpEntity<ArticleRequest> updateEntity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Result<ArticleResponse>> updateResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles/" + created.getId()),
            HttpMethod.PUT,
            updateEntity,
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertTrue(updateResponse.getBody().isSuccess());
        assertEquals("Updated Title", updateResponse.getBody().getData().getTitle());
    }

    @Test
    void deleteArticle() {
        // 先创建文章
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Article to Delete");
        request.setContent("Content");
        request.setSummary("Summary");
        request.setCategoryId(1L);
        request.setTags("test");

        HttpEntity<ArticleRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Result<ArticleResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        ArticleResponse created = createResponse.getBody().getData();

        // 删除文章
        ResponseEntity<Result<Void>> deleteResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles/" + created.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Void>>() {}
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().isSuccess());

        // 验证文章已被删除
        ResponseEntity<Result<ArticleResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles/" + created.getId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void listArticles() {
        // 创建多篇文章
        for (int i = 0; i < 3; i++) {
            ArticleRequest request = new ArticleRequest();
            request.setTitle("Article " + i);
            request.setContent("Content " + i);
            request.setSummary("Summary " + i);
            request.setCategoryId(1L);
            request.setTags("test");

            HttpEntity<ArticleRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.exchange(
                createURLWithPort("/api/v1/articles"),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Result<ArticleResponse>>() {}
            );
        }

        // 获取文章列表
        ResponseEntity<Result<Page<ArticleResponse>>> listResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/articles?page=0&size=10"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Page<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().isSuccess());
        assertTrue(listResponse.getBody().getData().getTotalElements() >= 3);
    }

    @Test
    void searchArticles() {
        // 创建一篇包含特定关键词的文章
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Unique Search Title");
        request.setContent("This is a unique content for search testing");
        request.setSummary("Unique summary");
        request.setCategoryId(1L);
        request.setTags("test,search");

        HttpEntity<ArticleRequest> entity = new HttpEntity<>(request, headers);

        restTemplate.exchange(
            createURLWithPort("/api/v1/articles"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<ArticleResponse>>() {}
        );

        // 搜索文章
        ResponseEntity<Result<Page<ArticleResponse>>> searchResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/search/articles?keyword=unique&page=0&size=10"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Page<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
        assertNotNull(searchResponse.getBody());
        assertTrue(searchResponse.getBody().isSuccess());
        assertTrue(searchResponse.getBody().getData().getTotalElements() >= 1);
    }
}
