package com.willamsc.personalwebsite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class CommentResponse {
    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 父评论用户名
     */
    private String parentUsername;

    /**
     * 回复的用户ID
     */
    private Long replyUserId;

    /**
     * 回复的用户名
     */
    private String replyUsername;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论状态：0待审核，1通过，2拒绝
     */
    private Integer status;

    /**
     * 子评论列表
     */
    private List<CommentResponse> children;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
