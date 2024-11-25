package com.willamsc.personalwebsite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类响应DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class CategoryResponse {
    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 父分类名称
     */
    private String parentName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 子分类列表
     */
    private List<CategoryResponse> children;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
