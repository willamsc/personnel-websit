package com.willamsc.personalwebsite.task;

import com.willamsc.personalwebsite.entity.ArticleStatistics;
import com.willamsc.personalwebsite.mapper.ArticleStatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 文章统计数据同步任务
 *
 * @author william
 * @since 2024-11-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleStatisticsSyncTask {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ArticleStatisticsMapper articleStatisticsMapper;

    private static final String ARTICLE_VIEW_COUNT_KEY = "article:view:*";
    private static final String ARTICLE_LIKE_SET_KEY = "article:like:*";
    private static final String ARTICLE_FAVORITE_SET_KEY = "article:favorite:*";

    /**
     * 每天凌晨2点执行同步任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncArticleStatistics() {
        log.info("开始同步文章统计数据到数据库");
        try {
            syncViewCounts();
            syncLikeCounts();
            syncFavoriteCounts();
            log.info("文章统计数据同步完成");
        } catch (Exception e) {
            log.error("文章统计数据同步失败", e);
            throw e;
        }
    }

    private void syncViewCounts() {
        Set<String> keys = redisTemplate.keys(ARTICLE_VIEW_COUNT_KEY);
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                Long articleId = extractArticleId(key);
                Object viewCount = redisTemplate.opsForValue().get(key);
                if (viewCount != null) {
                    ArticleStatistics statistics = articleStatisticsMapper.selectById(articleId);
                    if (statistics == null) {
                        statistics = new ArticleStatistics();
                        statistics.setArticleId(articleId);
                        statistics.setViewCount(Long.parseLong(viewCount.toString()));
                        statistics.setLikeCount(0L);
                        statistics.setCommentCount(0L);
                        statistics.setFavoriteCount(0L);
                        statistics.setShareCount(0L);
                        articleStatisticsMapper.insert(statistics);
                    } else {
                        statistics.setViewCount(Long.parseLong(viewCount.toString()));
                        articleStatisticsMapper.updateById(statistics);
                    }
                }
            } catch (Exception e) {
                log.error("同步文章浏览量失败，key: {}", key, e);
            }
        }
    }

    private void syncLikeCounts() {
        Set<String> keys = redisTemplate.keys(ARTICLE_LIKE_SET_KEY);
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                Long articleId = extractArticleId(key);
                Long likeCount = redisTemplate.opsForSet().size(key);
                if (likeCount != null) {
                    ArticleStatistics statistics = articleStatisticsMapper.selectById(articleId);
                    if (statistics == null) {
                        statistics = new ArticleStatistics();
                        statistics.setArticleId(articleId);
                        statistics.setViewCount(0L);
                        statistics.setLikeCount(likeCount);
                        statistics.setCommentCount(0L);
                        statistics.setFavoriteCount(0L);
                        statistics.setShareCount(0L);
                        articleStatisticsMapper.insert(statistics);
                    } else {
                        statistics.setLikeCount(likeCount);
                        articleStatisticsMapper.updateById(statistics);
                    }
                }
            } catch (Exception e) {
                log.error("同步文章点赞数失败，key: {}", key, e);
            }
        }
    }

    private void syncFavoriteCounts() {
        Set<String> keys = redisTemplate.keys(ARTICLE_FAVORITE_SET_KEY);
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                Long articleId = extractArticleId(key);
                Long favoriteCount = redisTemplate.opsForSet().size(key);
                if (favoriteCount != null) {
                    ArticleStatistics statistics = articleStatisticsMapper.selectById(articleId);
                    if (statistics == null) {
                        statistics = new ArticleStatistics();
                        statistics.setArticleId(articleId);
                        statistics.setViewCount(0L);
                        statistics.setLikeCount(0L);
                        statistics.setCommentCount(0L);
                        statistics.setFavoriteCount(favoriteCount);
                        statistics.setShareCount(0L);
                        articleStatisticsMapper.insert(statistics);
                    } else {
                        statistics.setFavoriteCount(favoriteCount);
                        articleStatisticsMapper.updateById(statistics);
                    }
                }
            } catch (Exception e) {
                log.error("同步文章收藏数失败，key: {}", key, e);
            }
        }
    }

    private Long extractArticleId(String key) {
        return Long.parseLong(key.substring(key.lastIndexOf(":") + 1));
    }
}
