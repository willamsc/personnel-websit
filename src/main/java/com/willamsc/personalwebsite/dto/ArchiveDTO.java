package com.willamsc.personalwebsite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文章归档DTO
 *
 * @author william
 * @since 2024-11-25
 */
@Data
@Schema(description = "文章归档")
public class ArchiveDTO {

    @Schema(description = "日期（年份或年月）")
    private String date;

    @Schema(description = "文章数量")
    private Integer count;

    @Schema(description = "文章列表")
    private List<ArticleResponse> articles;
}
