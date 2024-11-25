package com.willamsc.personalwebsite.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.willamsc.personalwebsite.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 评论实体
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class Comment extends BaseEntity {

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 评论者ID
     */
    private Long userId;

    /**
     * 父评论ID，如果是顶级评论则为null
     */
    private Long parentId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论状态
     * 0: 待审核
     * 1: 已通过
     * 2: 已拒绝
     */
    private Integer status;

    /**
     * 评论者信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;

    /**
     * 父评论信息（非数据库字段）
     */
    @TableField(exist = false)
    private Comment parent;

    /**
     * 子评论列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Comment> children;
}
