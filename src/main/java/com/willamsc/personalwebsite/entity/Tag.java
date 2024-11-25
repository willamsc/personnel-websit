package com.willamsc.personalwebsite.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.willamsc.personalwebsite.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签实体
 * 
 * @author william
 * @since 2024-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
public class Tag extends BaseEntity {

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
}
