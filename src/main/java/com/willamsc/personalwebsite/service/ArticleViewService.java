package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 文章阅读量服务
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ArticleService articleService;

    private static final String VIEW_COUNT_KEY = "article:view:count:";
    private static final String VIEW_IP_KEY = "article:view:ip:";
    private static final String HOT_ARTICLES_KEY = "article:hot";
    private static final long IP_EXPIRE_TIME = 12; // 12小时内同一IP重复访问不计数

    /**
     * 增加文章阅读量
     *
     * @param articleId 文章ID
     * @param ip 访问者IP
     */
    public void incrementViewCount(Long articleId, String ip) {
        String ipKey = VIEW_IP_KEY + articleId + ":" + ip;
        Boolean isFirstView = redisTemplate.opsForValue().setIfAbsent(ipKey, "1", IP_EXPIRE_TIME, TimeUnit.HOURS);
        
        if (Boolean.TRUE.equals(isFirstView)) {
            // 增加文章阅读量
            redisTemplate.opsForValue().increment(VIEW_COUNT_KEY + articleId);
            // 更新热门文章排行榜
            redisTemplate.opsForZSet().incrementScore(HOT_ARTICLES_KEY, articleId, 1);
        }
    }

    /**
     * 获取文章阅读量
     *
     * @param articleId 文章ID
     * @return 阅读量
     */
    public Integer getViewCount(Long articleId) {
        Object count = redisTemplate.opsForValue().get(VIEW_COUNT_KEY + articleId);
        return count == null ? 0 : Integer.parseInt(count.toString());
    }

    /**
     * 获取热门文章
     *
     * @param limit 获取数量
     * @return 文章ID集合
     */
    public Set<Object> getHotArticles(int limit) {
        return redisTemplate.opsForZSet().reverseRange(HOT_ARTICLES_KEY, 0, limit - 1);
    }

    /**
     * 初始化文章阅读量
     *
     * @param article 文章
     */
    public void initViewCount(Article article) {
        String key = VIEW_COUNT_KEY + article.getId();
        redisTemplate.opsForValue().setIfAbsent(key, article.getViewCount());
        redisTemplate.opsForZSet().add(HOT_ARTICLES_KEY, article.getId(), article.getViewCount());
    }

    /**
     * 定时同步阅读量到数据库
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void syncViewCountToDb() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY + "*");
        if (keys != null) {
            for (String key : keys) {
                Object count = redisTemplate.opsForValue().get(key);
                if (count != null) {
                    Long articleId = Long.valueOf(key.substring(VIEW_COUNT_KEY.length()));
                    articleService.updateViewCount(articleId, Integer.parseInt(count.toString()));
                }
            }
        }
    }
}
