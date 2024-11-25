package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.BaseTest;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文章点赞服务测试类
 *
 * @author william
 * @since 2024-11-25
 */
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class ArticleLikeServiceTest extends BaseTest {

    @Autowired
    private ArticleLikeService likeService;

    @Autowired
    private ArticleService articleService;

    private Long articleId;
    private Long userId = 1L;

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
    }

    @Test
    void likeArticle() {
        // 点赞文章
        likeService.likeArticle(articleId, userId);
        
        // 验证点赞状态
        assertTrue(likeService.hasLiked(articleId, userId));
        assertTrue(likeService.getLikeCount(articleId) > 0);
    }

    @Test
    void unlikeArticle() {
        // 先点赞
        likeService.likeArticle(articleId, userId);
        
        // 取消点赞
        likeService.unlikeArticle(articleId, userId);
        
        // 验证点赞状态
        assertFalse(likeService.hasLiked(articleId, userId));
    }

    @Test
    void likeNonExistentArticle() {
        Long nonExistentArticleId = 9999L;
        
        assertThrows(BusinessException.class, () -> {
            likeService.likeArticle(nonExistentArticleId, userId);
        });
    }

    @Test
    void getLikeCount() {
        // 初始点赞数应该为0
        assertEquals(0, likeService.getLikeCount(articleId));
        
        // 点赞后应该为1
        likeService.likeArticle(articleId, userId);
        assertEquals(1, likeService.getLikeCount(articleId));
        
        // 取消点赞后应该为0
        likeService.unlikeArticle(articleId, userId);
        assertEquals(0, likeService.getLikeCount(articleId));
    }

    @Test
    void hasLiked() {
        // 初始状态应该为未点赞
        assertFalse(likeService.hasLiked(articleId, userId));
        
        // 点赞后应该为已点赞
        likeService.likeArticle(articleId, userId);
        assertTrue(likeService.hasLiked(articleId, userId));
    }

    @Test
    void multipleLikes() {
        // 测试多个用户点赞
        Long userId2 = 2L;
        Long userId3 = 3L;
        
        likeService.likeArticle(articleId, userId);
        likeService.likeArticle(articleId, userId2);
        likeService.likeArticle(articleId, userId3);
        
        assertEquals(3, likeService.getLikeCount(articleId));
    }
}
