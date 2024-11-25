package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.BaseTest;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章服务测试类
 *
 * @author william
 * @since 2024-11-25
 */
class ArticleServiceTest extends BaseTest {

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
    void createArticle() {
        ArticleResponse response = articleService.createArticle(createArticleRequest);
        
        assertNotNull(response);
        assertEquals(createArticleRequest.getTitle(), response.getTitle());
        assertEquals(createArticleRequest.getContent(), response.getContent());
        assertEquals(createArticleRequest.getSummary(), response.getSummary());
        assertEquals(createArticleRequest.getCategoryId(), response.getCategoryId());
        assertEquals(createArticleRequest.getTags(), response.getTags());
    }

    @Test
    void createArticleWithInvalidTitle() {
        createArticleRequest.setTitle("");
        
        assertThrows(BusinessException.class, () -> {
            articleService.createArticle(createArticleRequest);
        });
    }

    @Test
    void updateArticle() {
        // 先创建文章
        ArticleResponse created = articleService.createArticle(createArticleRequest);
        
        // 更新文章
        createArticleRequest.setTitle("Updated Title");
        ArticleResponse updated = articleService.updateArticle(created.getId(), createArticleRequest);
        
        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals(created.getId(), updated.getId());
    }

    @Test
    void deleteArticle() {
        // 先创建文章
        ArticleResponse created = articleService.createArticle(createArticleRequest);
        
        // 删除文章
        articleService.deleteArticle(created.getId());
        
        // 验证删除
        assertThrows(BusinessException.class, () -> {
            articleService.getArticle(created.getId());
        });
    }

    @Test
    void getArticle() {
        // 先创建文章
        ArticleResponse created = articleService.createArticle(createArticleRequest);
        
        // 获取文章
        ArticleResponse retrieved = articleService.getArticle(created.getId());
        
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getTitle(), retrieved.getTitle());
    }

    @Test
    void listArticles() {
        // 创建多篇文章
        articleService.createArticle(createArticleRequest);
        
        createArticleRequest.setTitle("Test Article 2");
        articleService.createArticle(createArticleRequest);
        
        // 分页获取文章列表
        Page<ArticleResponse> page = articleService.listArticles(PageRequest.of(0, 10));
        
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 2);
    }

    @Test
    void searchArticles() {
        // 创建文章
        articleService.createArticle(createArticleRequest);
        
        // 搜索文章
        Page<ArticleResponse> page = articleService.searchArticles("Test", PageRequest.of(0, 10));
        
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 1);
    }

    @Test
    void getArticlesByCategory() {
        // 创建文章
        articleService.createArticle(createArticleRequest);
        
        // 按分类获取文章
        Page<ArticleResponse> page = articleService.getArticlesByCategory(
            createArticleRequest.getCategoryId(), 
            PageRequest.of(0, 10)
        );
        
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 1);
    }

    @Test
    void getArticlesByTag() {
        // 创建文章
        articleService.createArticle(createArticleRequest);
        
        // 按标签获取文章
        Page<ArticleResponse> page = articleService.getArticlesByTag("test", PageRequest.of(0, 10));
        
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 1);
    }
}
