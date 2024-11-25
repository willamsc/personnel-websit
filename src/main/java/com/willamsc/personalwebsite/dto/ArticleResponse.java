package com.willamsc.personalwebsite.dto;

import com.willamsc.personalwebsite.entity.Category;
import com.willamsc.personalwebsite.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章响应DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class ArticleResponse {
    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章封面图
     */
    private String coverImage;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 分类
     */
    private Category category;

    /**
     * 标签列表
     */
    private List<Tag> tags;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否允许评论
     */
    private Boolean allowComment;

    /**
     * 文章状态：0草稿，1发布，2归档
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
