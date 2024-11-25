package com.willamsc.personalwebsite.integration;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章归档功能集成测试
 *
 * @author william
 * @since 2024-11-25
 */
class ArticleArchiveIntegrationTest extends BaseIntegrationTest {

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
        // 创建跨越多个月份的文章
        LocalDate[] dates = {
            LocalDate.now(),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(3)
        };

        for (int i = 0; i < dates.length; i++) {
            ArticleRequest request = new ArticleRequest();
            request.setTitle("Article " + (i + 1));
            request.setContent("Content for article " + (i + 1));
            request.setSummary("Summary for article " + (i + 1));
            request.setCategoryId(1L);
            request.setTags("test");
            request.setPublishDate(dates[i].format(DateTimeFormatter.ISO_DATE));

            restTemplate.exchange(
                createURLWithPort("/api/v1/articles"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<Result<ArticleResponse>>() {}
            );
        }
    }

    @Test
    void getArchivesByYear() {
        int year = LocalDate.now().getYear();

        ResponseEntity<Result<Map<String, List<ArchiveDTO>>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/archives/year/" + year),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<Map<String, List<ArchiveDTO>>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        Map<String, List<ArchiveDTO>> archives = response.getBody().getData();
        assertFalse(archives.isEmpty());

        // 验证每个月份的文章
        archives.forEach((month, articles) -> {
            assertFalse(articles.isEmpty());
            articles.forEach(article -> {
                assertTrue(article.getPublishDate().startsWith(year + "-" + month));
            });
        });
    }

    @Test
    void getArchivesByMonth() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        ResponseEntity<Result<List<ArchiveDTO>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/archives/month/" + year + "/" + month),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArchiveDTO>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        List<ArchiveDTO> archives = response.getBody().getData();
        assertFalse(archives.isEmpty());

        // 验证文章日期
        archives.forEach(article -> {
            String date = article.getPublishDate();
            assertTrue(date.startsWith(year + "-" + String.format("%02d", month)));
        });
    }

    @Test
    void getArchivesSummary() {
        ResponseEntity<Result<List<ArchiveSummaryDTO>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/archives/summary"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArchiveSummaryDTO>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        List<ArchiveSummaryDTO> summaries = response.getBody().getData();
        assertFalse(summaries.isEmpty());

        // 验证摘要信息
        summaries.forEach(summary -> {
            assertNotNull(summary.getYear());
            assertNotNull(summary.getMonth());
            assertTrue(summary.getCount() > 0);
        });
    }

    @Test
    void getArchivesWithPagination() {
        int page = 0;
        int size = 2;

        ResponseEntity<Result<PageResponse<ArchiveDTO>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/archives/page?page=" + page + "&size=" + size),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<PageResponse<ArchiveDTO>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        PageResponse<ArchiveDTO> pageResponse = response.getBody().getData();
        assertNotNull(pageResponse);
        assertNotNull(pageResponse.getContent());
        assertTrue(pageResponse.getContent().size() <= size);
    }

    @Test
    void getArchivesByDateRange() {
        String startDate = LocalDate.now().minusMonths(3).format(DateTimeFormatter.ISO_DATE);
        String endDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        ResponseEntity<Result<List<ArchiveDTO>>> response = restTemplate.exchange(
            createURLWithPort("/api/v1/archives/range?startDate=" + startDate + "&endDate=" + endDate),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Result<List<ArchiveDTO>>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        List<ArchiveDTO> archives = response.getBody().getData();
        assertFalse(archives.isEmpty());

        // 验证日期范围
        archives.forEach(article -> {
            String date = article.getPublishDate();
            assertTrue(date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0);
        });
    }
}
