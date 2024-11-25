package com.willamsc.personalwebsite.integration;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类接口集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class CategoryIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        // 登录管理员账号
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Result<String>> loginResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/auth/login"),
            HttpMethod.POST,
            new HttpEntity<>(loginRequest, headers),
            new ParameterizedTypeReference<Result<String>>() {}
        );

        adminToken = loginResponse.getBody().getData();
        headers.setBearerAuth(adminToken);
    }

    @Test
    void createAndGetCategory() {
        // 创建分类
        CategoryRequest request = new CategoryRequest();
        request.setName("Test Category");
        request.setDescription("Test Category Description");

        HttpEntity<CategoryRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Result<CategoryResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertTrue(createResponse.getBody().isSuccess());

        CategoryResponse created = createResponse.getBody().getData();
        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getDescription(), created.getDescription());

        // 获取分类
        ResponseEntity<Result<CategoryResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories/" + created.getId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertTrue(getResponse.getBody().isSuccess());
        assertEquals(created.getId(), getResponse.getBody().getData().getId());
    }

    @Test
    void updateCategory() {
        // 先创建分类
        CategoryRequest createRequest = new CategoryRequest();
        createRequest.setName("Original Category");
        createRequest.setDescription("Original Description");

        ResponseEntity<Result<CategoryResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories"),
            HttpMethod.POST,
            new HttpEntity<>(createRequest, headers),
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        Long categoryId = createResponse.getBody().getData().getId();

        // 更新分类
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setDescription("Updated Description");

        ResponseEntity<Result<CategoryResponse>> updateResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories/" + categoryId),
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest, headers),
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertTrue(updateResponse.getBody().isSuccess());
        assertEquals(updateRequest.getName(), updateResponse.getBody().getData().getName());
        assertEquals(updateRequest.getDescription(), updateResponse.getBody().getData().getDescription());
    }

    @Test
    void listCategories() {
        // 创建多个分类
        for (int i = 0; i < 3; i++) {
            CategoryRequest request = new CategoryRequest();
            request.setName("Category " + i);
            request.setDescription("Description " + i);

            restTemplate.exchange(
                createURLWithPort("/api/v1/categories"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<Result<CategoryResponse>>() {}
            );
        }

        // 获取分类列表
        ResponseEntity<Result<List<CategoryResponse>>> listResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<CategoryResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().isSuccess());
        assertTrue(listResponse.getBody().getData().size() >= 3);
    }

    @Test
    void deleteCategory() {
        // 创建分类
        CategoryRequest request = new CategoryRequest();
        request.setName("Category to Delete");
        request.setDescription("Description");

        ResponseEntity<Result<CategoryResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories"),
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        Long categoryId = createResponse.getBody().getData().getId();

        // 删除分类
        ResponseEntity<Result<Void>> deleteResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories/" + categoryId),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Void>>() {}
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().isSuccess());

        // 验证分类已被删除
        ResponseEntity<Result<CategoryResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories/" + categoryId),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void getCategoryArticles() {
        // 创建分类
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Test Category");
        categoryRequest.setDescription("Test Description");

        ResponseEntity<Result<CategoryResponse>> categoryResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories"),
            HttpMethod.POST,
            new HttpEntity<>(categoryRequest, headers),
            new ParameterizedTypeReference<Result<CategoryResponse>>() {}
        );

        Long categoryId = categoryResponse.getBody().getData().getId();

        // 创建属于该分类的文章
        for (int i = 0; i < 3; i++) {
            ArticleRequest articleRequest = new ArticleRequest();
            articleRequest.setTitle("Article " + i);
            articleRequest.setContent("Content " + i);
            articleRequest.setSummary("Summary " + i);
            articleRequest.setCategoryId(categoryId);
            articleRequest.setTags("test");

            restTemplate.exchange(
                createURLWithPort("/api/v1/articles"),
                HttpMethod.POST,
                new HttpEntity<>(articleRequest, headers),
                new ParameterizedTypeReference<Result<ArticleResponse>>() {}
            );
        }

        // 获取分类下的文章列表
        ResponseEntity<Result<List<ArticleResponse>>> listResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/categories/" + categoryId + "/articles"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().isSuccess());
        assertTrue(listResponse.getBody().getData().size() >= 3);
        listResponse.getBody().getData().forEach(article -> 
            assertEquals(categoryId, article.getCategoryId())
        );
    }
}
