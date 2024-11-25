package com.willamsc.personalwebsite.dto;

import lombok.Data;

/**
 * 文章统计信息DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class ArticleStatisticsDTO {
    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 浏览量
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 分享数
     */
    private Long shareCount;

    /**
     * 当前用户是否点赞
     */
    private Boolean liked;

    /**
     * 当前用户是否收藏
     */
    private Boolean favorited;
}
