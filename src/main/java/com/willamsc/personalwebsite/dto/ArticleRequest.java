package com.willamsc.personalwebsite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 文章请求DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class ArticleRequest {
    /**
     * 文章标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 文章内容
     */
    @NotBlank(message = "内容不能为空")
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
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 是否置顶
     */
    private Boolean isTop = false;

    /**
     * 是否允许评论
     */
    private Boolean allowComment = true;

    /**
     * 文章状态：0草稿，1发布，2归档
     */
    private Integer status = 0;
}
