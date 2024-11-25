package com.willamsc.personalwebsite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签响应DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class TagResponse {
    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 标签图标
     */
    private String icon;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 相关文章列表（用于标签详情页）
     */
    private List<ArticleResponse> articles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
