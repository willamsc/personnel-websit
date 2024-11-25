package com.willamsc.personalwebsite.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.willamsc.personalwebsite.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签关联实体
 * 用于维护文章和标签的多对多关系
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_tag")
public class ArticleTag extends BaseEntity {

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 标签ID
     */
    private Long tagId;
}
