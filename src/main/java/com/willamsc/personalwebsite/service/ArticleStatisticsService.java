package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.dto.ArticleStatisticsDTO;
import com.willamsc.personalwebsite.entity.ArticleStatistics;

/**
 * 文章统计服务接口
 *
 * @author william
 * @since 2024-11-25
 */
public interface ArticleStatisticsService {
    /**
     * 增加文章浏览量
     *
     * @param articleId 文章ID
     * @return 更新后的统计信息
     */
    ArticleStatisticsDTO incrementViewCount(Long articleId);

    /**
     * 点赞或取消点赞文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 更新后的统计信息
     */
    ArticleStatisticsDTO toggleLike(Long articleId, Long userId);

    /**
     * 收藏或取消收藏文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 更新后的统计信息
     */
    ArticleStatisticsDTO toggleFavorite(Long articleId, Long userId);

    /**
     * 增加文章分享数
     *
     * @param articleId 文章ID
     * @return 更新后的统计信息
     */
    ArticleStatisticsDTO incrementShareCount(Long articleId);

    /**
     * 获取文章统计信息
     *
     * @param articleId 文章ID
     * @param userId 当前用户ID，可以为null
     * @return 文章统计信息
     */
    ArticleStatisticsDTO getArticleStatistics(Long articleId, Long userId);

    /**
     * 更新文章评论数
     *
     * @param articleId 文章ID
     * @param increment 增量，可以为负数
     * @return 更新后的统计信息
     */
    ArticleStatisticsDTO updateCommentCount(Long articleId, int increment);

    /**
     * 初始化文章统计信息
     *
     * @param articleId 文章ID
     * @return 初始化的统计信息
     */
    ArticleStatistics initArticleStatistics(Long articleId);
}
