package com.willamsc.personalwebsite.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.willamsc.personalwebsite.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类实体
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

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
     * 排序号
     */
    private Integer sort;

    /**
     * 父分类ID，如果是顶级分类则为null
     */
    private Long parentId;
}
