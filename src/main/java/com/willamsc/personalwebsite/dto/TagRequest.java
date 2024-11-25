package com.willamsc.personalwebsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签请求DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
public class TagRequest {
    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 标签图标
     */
    private String icon;
}
