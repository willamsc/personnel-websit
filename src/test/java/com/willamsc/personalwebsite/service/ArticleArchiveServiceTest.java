package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.BaseTest;
import com.willamsc.personalwebsite.dto.ArchiveDTO;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.common.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章归档服务测试类
 *
 * @author william
 * @since 2024-11-25
 */
class ArticleArchiveServiceTest extends BaseTest {

    @Autowired
    private ArticleArchiveService archiveService;

    @Autowired
    private ArticleService articleService;

    private ArticleRequest createArticleRequest;

    @BeforeEach
    void setUp() {
        createArticleRequest = new ArticleRequest();
        createArticleRequest.setTitle("Test Article");
        createArticleRequest.setContent("Test Content");
        createArticleRequest.setSummary("Test Summary");
        createArticleRequest.setCategoryId(1L);
        createArticleRequest.setTags("test,unit test");
    }

    @Test
    void archiveByMonth() {
        // 创建测试文章
        articleService.createArticle(createArticleRequest);

        Result<List<ArchiveDTO>> result = archiveService.archiveByMonth();
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
        
        ArchiveDTO archive = result.getData().get(0);
        assertNotNull(archive.getDate());
        assertTrue(archive.getCount() > 0);
        assertFalse(archive.getArticles().isEmpty());
    }

    @Test
    void archiveByYear() {
        // 创建测试文章
        articleService.createArticle(createArticleRequest);

        Result<List<ArchiveDTO>> result = archiveService.archiveByYear();
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
        
        ArchiveDTO archive = result.getData().get(0);
        assertNotNull(archive.getDate());
        assertTrue(archive.getCount() > 0);
        assertFalse(archive.getArticles().isEmpty());
    }

    @Test
    void getArticlesByDateRange() {
        // 创建测试文章
        articleService.createArticle(createArticleRequest);

        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);

        Result<List<ArticleResponse>> result = archiveService.getArticlesByDateRange(startTime, endTime);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    void getTimeline() {
        // 创建测试文章
        articleService.createArticle(createArticleRequest);

        Result<Map<String, Integer>> result = archiveService.getTimeline();
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
        
        // 验证当前月份的文章数
        String currentMonth = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        assertTrue(result.getData().containsKey(currentMonth));
        assertTrue(result.getData().get(currentMonth) > 0);
    }
}
