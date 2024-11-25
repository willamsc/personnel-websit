package com.willamsc.personalwebsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类请求DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class CategoryRequest {
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
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
     * 排序
     */
    private Integer sort = 0;
}
