package com.willamsc.personalwebsite.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 基础实体类，提供所有实体通用的字段
 * 
 * @author william
 * @since 2024-02-20
 */
@Data
public class BaseEntity {
    /**
     * 主键ID，使用自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间，由MyBatis-Plus自动填充
     * 在插入时自动设置为当前时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间，由MyBatis-Plus自动填充
     * 在插入和更新时自动设置为当前时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     * 0: 未删除
     * 1: 已删除
     */
    @TableLogic
    private Integer deleted;
}
