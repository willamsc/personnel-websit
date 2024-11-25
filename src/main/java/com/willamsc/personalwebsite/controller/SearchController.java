package com.willamsc.personalwebsite.controller;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "搜索管理", description = "搜索相关接口")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "搜索文章")
    @GetMapping("/articles")
    public Result<Page<ArticleResponse>> searchArticles(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return searchService.searchArticles(keyword, pageNum, pageSize);
    }

    @Operation(summary = "获取相关文章")
    @GetMapping("/articles/{id}/related")
    public Result<List<ArticleResponse>> getRelatedArticles(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Parameter(description = "分类ID") @RequestParam Long categoryId,
            @Parameter(description = "标签ID列表") @RequestParam List<Long> tagIds,
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "5") Integer limit) {
        return searchService.findRelatedArticles(id, categoryId, tagIds, limit);
    }
}
