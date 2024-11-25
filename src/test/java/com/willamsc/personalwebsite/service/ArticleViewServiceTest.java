package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.BaseTest;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章浏览服务测试类
 *
 * @author william
 * @since 2024-11-25
 */
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class ArticleViewServiceTest extends BaseTest {

    @Autowired
    private ArticleViewService viewService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Long articleId;
    private String ipAddress = "127.0.0.1";

    @BeforeEach
    void setUp() {
        // 创建测试文章
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Test Article");
        request.setContent("Test Content");
        request.setSummary("Test Summary");
        request.setCategoryId(1L);
        request.setTags("test");
        
        ArticleResponse response = articleService.createArticle(request);
        articleId = response.getId();
        
        // 清理Redis测试数据
        redisTemplate.delete(viewService.getViewCountKey(articleId));
        redisTemplate.delete(viewService.getViewedIpKey(articleId));
    }

    @Test
    void incrementViewCount() {
        // 增加浏览量
        viewService.incrementViewCount(articleId, ipAddress);
        
        // 验证浏览量
        Long viewCount = viewService.getViewCount(articleId);
        assertEquals(1, viewCount);
    }

    @Test
    void incrementViewCountWithSameIp() {
        // 同一IP多次浏览
        viewService.incrementViewCount(articleId, ipAddress);
        viewService.incrementViewCount(articleId, ipAddress);
        
        // 验证浏览量应该仍然为1
        Long viewCount = viewService.getViewCount(articleId);
        assertEquals(1, viewCount);
    }

    @Test
    void incrementViewCountWithDifferentIps() {
        // 不同IP浏览
        viewService.incrementViewCount(articleId, "127.0.0.1");
        viewService.incrementViewCount(articleId, "127.0.0.2");
        
        // 验证浏览量应该为2
        Long viewCount = viewService.getViewCount(articleId);
        assertEquals(2, viewCount);
    }

    @Test
    void getViewCount() {
        // 增加浏览量
        viewService.incrementViewCount(articleId, ipAddress);
        
        // 获取浏览量
        Long viewCount = viewService.getViewCount(articleId);
        assertEquals(1, viewCount);
    }

    @Test
    void getHotArticles() {
        // 创建第二篇文章
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Second Article");
        request.setContent("Second Content");
        request.setSummary("Second Summary");
        request.setCategoryId(1L);
        request.setTags("test");
        ArticleResponse secondArticle = articleService.createArticle(request);
        
        // 增加不同的浏览量
        viewService.incrementViewCount(articleId, "127.0.0.1");
        viewService.incrementViewCount(articleId, "127.0.0.2");
        viewService.incrementViewCount(secondArticle.getId(), "127.0.0.1");
        
        // 获取热门文章
        List<ArticleResponse> hotArticles = viewService.getHotArticles(10);
        
        assertNotNull(hotArticles);
        assertFalse(hotArticles.isEmpty());
        // 第一篇文章应该排在前面（浏览量更多）
        assertEquals(articleId, hotArticles.get(0).getId());
    }

    @Test
    void syncViewCountToDb() {
        // 增加浏览量
        viewService.incrementViewCount(articleId, "127.0.0.1");
        viewService.incrementViewCount(articleId, "127.0.0.2");
        
        // 同步到数据库
        viewService.syncViewCountToDb();
        
        // 验证数据库中的浏览量
        ArticleResponse article = articleService.getArticle(articleId);
        assertEquals(2, article.getViewCount());
    }

    @Test
    void hasViewed() {
        // 初始状态应该是未浏览
        assertFalse(viewService.hasViewed(articleId, ipAddress));
        
        // 浏览后状态应该改变
        viewService.incrementViewCount(articleId, ipAddress);
        assertTrue(viewService.hasViewed(articleId, ipAddress));
    }
}
