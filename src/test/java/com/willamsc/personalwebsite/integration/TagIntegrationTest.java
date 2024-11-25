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
 * 标签接口集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class TagIntegrationTest extends BaseIntegrationTest {

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
    void createAndGetTag() {
        // 创建标签
        TagRequest request = new TagRequest();
        request.setName("Test Tag");
        request.setDescription("Test Tag Description");

        HttpEntity<TagRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Result<TagResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags"),
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertTrue(createResponse.getBody().isSuccess());

        TagResponse created = createResponse.getBody().getData();
        assertNotNull(created.getId());
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getDescription(), created.getDescription());

        // 获取标签
        ResponseEntity<Result<TagResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags/" + created.getId()),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertTrue(getResponse.getBody().isSuccess());
        assertEquals(created.getId(), getResponse.getBody().getData().getId());
    }

    @Test
    void updateTag() {
        // 先创建标签
        TagRequest createRequest = new TagRequest();
        createRequest.setName("Original Tag");
        createRequest.setDescription("Original Description");

        ResponseEntity<Result<TagResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags"),
            HttpMethod.POST,
            new HttpEntity<>(createRequest, headers),
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        Long tagId = createResponse.getBody().getData().getId();

        // 更新标签
        TagRequest updateRequest = new TagRequest();
        updateRequest.setName("Updated Tag");
        updateRequest.setDescription("Updated Description");

        ResponseEntity<Result<TagResponse>> updateResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags/" + tagId),
            HttpMethod.PUT,
            new HttpEntity<>(updateRequest, headers),
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertTrue(updateResponse.getBody().isSuccess());
        assertEquals(updateRequest.getName(), updateResponse.getBody().getData().getName());
        assertEquals(updateRequest.getDescription(), updateResponse.getBody().getData().getDescription());
    }

    @Test
    void listTags() {
        // 创建多个标签
        for (int i = 0; i < 3; i++) {
            TagRequest request = new TagRequest();
            request.setName("Tag " + i);
            request.setDescription("Description " + i);

            restTemplate.exchange(
                createURLWithPort("/api/v1/tags"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<Result<TagResponse>>() {}
            );
        }

        // 获取标签列表
        ResponseEntity<Result<List<TagResponse>>> listResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<TagResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().isSuccess());
        assertTrue(listResponse.getBody().getData().size() >= 3);
    }

    @Test
    void deleteTag() {
        // 创建标签
        TagRequest request = new TagRequest();
        request.setName("Tag to Delete");
        request.setDescription("Description");

        ResponseEntity<Result<TagResponse>> createResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags"),
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        Long tagId = createResponse.getBody().getData().getId();

        // 删除标签
        ResponseEntity<Result<Void>> deleteResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags/" + tagId),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Void>>() {}
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().isSuccess());

        // 验证标签已被删除
        ResponseEntity<Result<TagResponse>> getResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags/" + tagId),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void getTagArticles() {
        // 创建标签
        TagRequest tagRequest = new TagRequest();
        tagRequest.setName("Test Tag");
        tagRequest.setDescription("Test Description");

        ResponseEntity<Result<TagResponse>> tagResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags"),
            HttpMethod.POST,
            new HttpEntity<>(tagRequest, headers),
            new ParameterizedTypeReference<Result<TagResponse>>() {}
        );

        Long tagId = tagResponse.getBody().getData().getId();

        // 创建带有该标签的文章
        for (int i = 0; i < 3; i++) {
            ArticleRequest articleRequest = new ArticleRequest();
            articleRequest.setTitle("Article " + i);
            articleRequest.setContent("Content " + i);
            articleRequest.setSummary("Summary " + i);
            articleRequest.setCategoryId(1L);
            articleRequest.setTags(tagRequest.getName());

            restTemplate.exchange(
                createURLWithPort("/api/v1/articles"),
                HttpMethod.POST,
                new HttpEntity<>(articleRequest, headers),
                new ParameterizedTypeReference<Result<ArticleResponse>>() {}
            );
        }

        // 获取标签下的文章列表
        ResponseEntity<Result<List<ArticleResponse>>> listResponse = restTemplate.exchange(
            createURLWithPort("/api/v1/tags/" + tagId + "/articles"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        assertTrue(listResponse.getBody().isSuccess());
        assertTrue(listResponse.getBody().getData().size() >= 3);
        listResponse.getBody().getData().forEach(article -> 
            assertTrue(article.getTags().contains(tagRequest.getName()))
        );
    }
}
