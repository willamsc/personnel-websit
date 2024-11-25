package com.willamsc.personalwebsite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论请求DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class CommentRequest {
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 回复的用户ID
     */
    private Long replyUserId;
}
