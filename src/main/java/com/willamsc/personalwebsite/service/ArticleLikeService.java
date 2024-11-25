package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 文章点赞服务
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ArticleService articleService;

    private static final String LIKE_COUNT_KEY = "article:like:count:";
    private static final String USER_LIKE_KEY = "article:like:user:";
    private static final String LIKED_ARTICLES_KEY = "user:liked:articles:";
    private static final long LIKE_EXPIRE_TIME = 30; // 30天后过期

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞结果
     */
    public Result<Boolean> likeArticle(Long articleId, Long userId) {
        String countKey = LIKE_COUNT_KEY + articleId;
        String userKey = USER_LIKE_KEY + articleId;
        String userArticlesKey = LIKED_ARTICLES_KEY + userId;

        Boolean isLiked = redisTemplate.opsForSet().isMember(userKey, userId);
        if (Boolean.TRUE.equals(isLiked)) {
            // 取消点赞
            redisTemplate.opsForSet().remove(userKey, userId);
            redisTemplate.opsForValue().decrement(countKey);
            redisTemplate.opsForSet().remove(userArticlesKey, articleId);
            return Result.success(false);
        } else {
            // 点赞
            redisTemplate.opsForSet().add(userKey, userId);
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.opsForSet().add(userArticlesKey, articleId);
            // 设置过期时间
            redisTemplate.expire(userKey, LIKE_EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.expire(countKey, LIKE_EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.expire(userArticlesKey, LIKE_EXPIRE_TIME, TimeUnit.DAYS);
            return Result.success(true);
        }
    }

    /**
     * 获取文章点赞数
     *
     * @param articleId 文章ID
     * @return 点赞数
     */
    public Integer getLikeCount(Long articleId) {
        Object count = redisTemplate.opsForValue().get(LIKE_COUNT_KEY + articleId);
        return count == null ? 0 : Integer.parseInt(count.toString());
    }

    /**
     * 检查用户是否点赞
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    public Boolean isLiked(Long articleId, Long userId) {
        return redisTemplate.opsForSet().isMember(USER_LIKE_KEY + articleId, userId);
    }

    /**
     * 获取用户点赞的文章
     *
     * @param userId 用户ID
     * @return 文章ID集合
     */
    public Set<Object> getUserLikedArticles(Long userId) {
        return redisTemplate.opsForSet().members(LIKED_ARTICLES_KEY + userId);
    }

    /**
     * 初始化文章点赞数
     *
     * @param article 文章
     */
    public void initLikeCount(Article article) {
        String key = LIKE_COUNT_KEY + article.getId();
        redisTemplate.opsForValue().setIfAbsent(key, article.getLikeCount());
    }

    /**
     * 定时同步点赞数到数据库
     * 每天凌晨4点执行
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void syncLikeCountToDb() {
        Set<String> keys = redisTemplate.keys(LIKE_COUNT_KEY + "*");
        if (keys != null) {
            for (String key : keys) {
                Object count = redisTemplate.opsForValue().get(key);
                if (count != null) {
                    Long articleId = Long.valueOf(key.substring(LIKE_COUNT_KEY.length()));
                    articleService.updateLikeCount(articleId, Integer.parseInt(count.toString()));
                }
            }
        }
    }
}
