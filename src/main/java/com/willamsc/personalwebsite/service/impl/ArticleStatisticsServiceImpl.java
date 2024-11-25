package com.willamsc.personalwebsite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willamsc.personalwebsite.dto.ArticleStatisticsDTO;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.entity.ArticleStatistics;
import com.willamsc.personalwebsite.exception.BusinessException;
import com.willamsc.personalwebsite.exception.ErrorCode;
import com.willamsc.personalwebsite.mapper.ArticleMapper;
import com.willamsc.personalwebsite.mapper.ArticleStatisticsMapper;
import com.willamsc.personalwebsite.service.ArticleStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 文章统计服务实现类
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class ArticleStatisticsServiceImpl extends ServiceImpl<ArticleStatisticsMapper, ArticleStatistics>
        implements ArticleStatisticsService {

    private final ArticleMapper articleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ARTICLE_VIEW_COUNT_KEY = "article:view:";
    private static final String ARTICLE_LIKE_SET_KEY = "article:like:";
    private static final String ARTICLE_FAVORITE_SET_KEY = "article:favorite:";
    private static final long CACHE_EXPIRE_DAYS = 7;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleStatisticsDTO incrementViewCount(Long articleId) {
        // 检查文章是否存在
        checkArticleExists(articleId);

        // 使用Redis计数器增加浏览量
        String key = ARTICLE_VIEW_COUNT_KEY + articleId;
        Long viewCount = redisTemplate.opsForValue().increment(key);
        if (viewCount == 1) {
            // 如果是第一次访问，设置过期时间
            redisTemplate.expire(key, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        }

        // 定期同步到数据库（这里简单处理，实际应该用定时任务批量同步）
        if (viewCount % 10 == 0) {
            ArticleStatistics statistics = getBaseMapper().selectById(articleId);
            if (statistics == null) {
                statistics = initArticleStatistics(articleId);
            }
            statistics.setViewCount(viewCount);
            getBaseMapper().updateById(statistics);
        }

        return getArticleStatistics(articleId, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleStatisticsDTO toggleLike(Long articleId, Long userId) {
        checkArticleExists(articleId);
        String key = ARTICLE_LIKE_SET_KEY + articleId;

        Boolean isLiked = redisTemplate.opsForSet().isMember(key, userId);
        if (Boolean.TRUE.equals(isLiked)) {
            // 取消点赞
            redisTemplate.opsForSet().remove(key, userId);
            updateStatistics(articleId, "like_count", -1);
        } else {
            // 添加点赞
            redisTemplate.opsForSet().add(key, userId);
            redisTemplate.expire(key, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
            updateStatistics(articleId, "like_count", 1);
        }

        return getArticleStatistics(articleId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleStatisticsDTO toggleFavorite(Long articleId, Long userId) {
        checkArticleExists(articleId);
        String key = ARTICLE_FAVORITE_SET_KEY + articleId;

        Boolean isFavorited = redisTemplate.opsForSet().isMember(key, userId);
        if (Boolean.TRUE.equals(isFavorited)) {
            // 取消收藏
            redisTemplate.opsForSet().remove(key, userId);
            updateStatistics(articleId, "favorite_count", -1);
        } else {
            // 添加收藏
            redisTemplate.opsForSet().add(key, userId);
            redisTemplate.expire(key, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
            updateStatistics(articleId, "favorite_count", 1);
        }

        return getArticleStatistics(articleId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleStatisticsDTO incrementShareCount(Long articleId) {
        checkArticleExists(articleId);
        updateStatistics(articleId, "share_count", 1);
        return getArticleStatistics(articleId, null);
    }

    @Override
    public ArticleStatisticsDTO getArticleStatistics(Long articleId, Long userId) {
        ArticleStatistics statistics = getBaseMapper().selectById(articleId);
        if (statistics == null) {
            statistics = initArticleStatistics(articleId);
        }

        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        ArticleStatisticsDTO dto = new ArticleStatisticsDTO();
        dto.setArticleId(articleId);
        dto.setTitle(article.getTitle());
        dto.setViewCount(getViewCount(articleId));
        dto.setLikeCount(getLikeCount(articleId));
        dto.setCommentCount(statistics.getCommentCount());
        dto.setFavoriteCount(getFavoriteCount(articleId));
        dto.setShareCount(statistics.getShareCount());

        if (userId != null) {
            dto.setLiked(isLiked(articleId, userId));
            dto.setFavorited(isFavorited(articleId, userId));
        }

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleStatisticsDTO updateCommentCount(Long articleId, int increment) {
        checkArticleExists(articleId);
        updateStatistics(articleId, "comment_count", increment);
        return getArticleStatistics(articleId, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleStatistics initArticleStatistics(Long articleId) {
        ArticleStatistics statistics = new ArticleStatistics();
        statistics.setArticleId(articleId);
        statistics.setViewCount(0L);
        statistics.setLikeCount(0L);
        statistics.setCommentCount(0L);
        statistics.setFavoriteCount(0L);
        statistics.setShareCount(0L);
        getBaseMapper().insert(statistics);
        return statistics;
    }

    private void checkArticleExists(Long articleId) {
        if (articleMapper.selectById(articleId) == null) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }
    }

    private void updateStatistics(Long articleId, String field, int increment) {
        ArticleStatistics statistics = getBaseMapper().selectById(articleId);
        if (statistics == null) {
            statistics = initArticleStatistics(articleId);
        }

        switch (field) {
            case "like_count":
                statistics.setLikeCount(statistics.getLikeCount() + increment);
                break;
            case "comment_count":
                statistics.setCommentCount(statistics.getCommentCount() + increment);
                break;
            case "favorite_count":
                statistics.setFavoriteCount(statistics.getFavoriteCount() + increment);
                break;
            case "share_count":
                statistics.setShareCount(statistics.getShareCount() + increment);
                break;
            default:
                throw new IllegalArgumentException("Invalid field: " + field);
        }

        getBaseMapper().updateById(statistics);
    }

    private Long getViewCount(Long articleId) {
        String key = ARTICLE_VIEW_COUNT_KEY + articleId;
        Object count = redisTemplate.opsForValue().get(key);
        if (count != null) {
            return Long.parseLong(count.toString());
        }
        ArticleStatistics statistics = getBaseMapper().selectById(articleId);
        return statistics != null ? statistics.getViewCount() : 0L;
    }

    private Long getLikeCount(Long articleId) {
        String key = ARTICLE_LIKE_SET_KEY + articleId;
        Long count = redisTemplate.opsForSet().size(key);
        if (count != null && count > 0) {
            return count;
        }
        ArticleStatistics statistics = getBaseMapper().selectById(articleId);
        return statistics != null ? statistics.getLikeCount() : 0L;
    }

    private Long getFavoriteCount(Long articleId) {
        String key = ARTICLE_FAVORITE_SET_KEY + articleId;
        Long count = redisTemplate.opsForSet().size(key);
        if (count != null && count > 0) {
            return count;
        }
        ArticleStatistics statistics = getBaseMapper().selectById(articleId);
        return statistics != null ? statistics.getFavoriteCount() : 0L;
    }

    private boolean isLiked(Long articleId, Long userId) {
        String key = ARTICLE_LIKE_SET_KEY + articleId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
    }

    private boolean isFavorited(Long articleId, Long userId) {
        String key = ARTICLE_FAVORITE_SET_KEY + articleId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
    }
}
