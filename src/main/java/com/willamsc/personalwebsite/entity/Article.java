package com.willamsc.personalwebsite.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.willamsc.personalwebsite.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseEntity {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 文章封面图
     */
    private String coverImage;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文章状态
     * 0: 草稿
     * 1: 已发布
     * 2: 已归档
     */
    private Integer status;

    /**
     * 是否置顶
     * 0: 否
     * 1: 是
     */
    private Integer isTop;

    /**
     * 是否允许评论
     * 0: 否
     * 1: 是
     */
    private Integer allowComment;

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
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 标签列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Tag> tags;

    /**
     * 作者信息（非数据库字段）
     */
    @TableField(exist = false)
    private User author;

    /**
     * 分类信息（非数据库字段）
     */
    @TableField(exist = false)
    private Category category;
}
