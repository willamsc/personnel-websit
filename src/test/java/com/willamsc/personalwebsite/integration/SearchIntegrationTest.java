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
 * 搜索功能集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class SearchIntegrationTest extends BaseIntegrationTest {

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

        // 创建测试文章
        createTestArticles();
    }

    private void createTestArticles() {
        String[] titles = {
            "Spring Boot Tutorial",
            "Java Programming Guide",
            "Elasticsearch Integration",
            "Redis Cache Implementation"
        };

        String[] contents = {
            "This is a comprehensive guide to Spring Boot framework",
            "Learn Java programming from basics to advanced topics",
            "How to integrate Elasticsearch with Spring Boot",
            "Implementing Redis cache in Spring Boot applications"
        };

        for (int i = 0; i < titles.length; i++) {
            ArticleRequest request = new ArticleRequest();
            request.setTitle(titles[i]);
            request.setContent(contents[i]);
            request.setSummary("Summary of " + titles[i]);
            request.setCategoryId(1L);
            request.setTags("test,tutorial");

            restTemplate.exchange(
                createURLWithPort("/api/v1/articles"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<Result<ArticleResponse>>() {}
            );
        }

        // 等待Elasticsearch索引更新
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void searchByKeyword() {
        String keyword = "Spring Boot";

        ResponseEntity<Result<List<ArticleResponse>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/search?keyword=" + keyword),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertFalse(response.getBody().getData().isEmpty());

        List<ArticleResponse> results = response.getBody().getData();
        assertTrue(results.stream().anyMatch(article -> 
            article.getTitle().contains(keyword) || article.getContent().contains(keyword)
        ));
    }

    @Test
    void searchByTag() {
        String tag = "tutorial";

        ResponseEntity<Result<List<ArticleResponse>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/search/tag?tag=" + tag),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertFalse(response.getBody().getData().isEmpty());

        List<ArticleResponse> results = response.getBody().getData();
        assertTrue(results.stream().allMatch(article -> 
            article.getTags().contains(tag)
        ));
    }

    @Test
    void searchWithPagination() {
        String keyword = "guide";
        int page = 0;
        int size = 2;

        ResponseEntity<Result<PageResponse<ArticleResponse>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/search/page?keyword=" + keyword + "&page=" + page + "&size=" + size),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<PageResponse<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        PageResponse<ArticleResponse> pageResponse = response.getBody().getData();
        assertNotNull(pageResponse);
        assertNotNull(pageResponse.getContent());
        assertTrue(pageResponse.getContent().size() <= size);
        assertTrue(pageResponse.getContent().stream().anyMatch(article ->
            article.getTitle().toLowerCase().contains(keyword) || 
            article.getContent().toLowerCase().contains(keyword)
        ));
    }

    @Test
    void searchWithHighlight() {
        String keyword = "elasticsearch";

        ResponseEntity<Result<List<ArticleResponse>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/search/highlight?keyword=" + keyword),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertFalse(response.getBody().getData().isEmpty());

        List<ArticleResponse> results = response.getBody().getData();
        assertTrue(results.stream().anyMatch(article ->
            article.getTitle().toLowerCase().contains(keyword) ||
            article.getContent().toLowerCase().contains(keyword)
        ));

        // 验证高亮结果包含HTML标签
        assertTrue(results.stream().anyMatch(article ->
            article.getHighlightContent() != null &&
            article.getHighlightContent().contains("<em>") &&
            article.getHighlightContent().contains("</em>")
        ));
    }

    @Test
    void searchWithAdvancedFilters() {
        SearchRequest request = new SearchRequest();
        request.setKeyword("spring");
        request.setTags(List.of("tutorial"));
        request.setStartDate("2024-01-01");
        request.setEndDate("2024-12-31");

        ResponseEntity<Result<List<ArticleResponse>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/search/advanced"),
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<Result<List<ArticleResponse>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        List<ArticleResponse> results = response.getBody().getData();
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(article ->
            article.getTags().containsAll(request.getTags()) &&
            (article.getTitle().toLowerCase().contains(request.getKeyword()) ||
             article.getContent().toLowerCase().contains(request.getKeyword()))
        ));
    }
}
