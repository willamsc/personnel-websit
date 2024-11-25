package com.willamsc.personalwebsite.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.dto.TagRequest;
import com.willamsc.personalwebsite.dto.TagResponse;
import com.willamsc.personalwebsite.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "标签管理", description = "标签相关接口")
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "创建标签")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        return tagService.createTag(request);
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TagResponse> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Valid @RequestBody TagRequest request) {
        return tagService.updateTag(id, request);
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteTag(@Parameter(description = "标签ID") @PathVariable Long id) {
        return tagService.deleteTag(id);
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public Result<TagResponse> getTag(@Parameter(description = "标签ID") @PathVariable Long id) {
        return tagService.getTag(id);
    }

    @Operation(summary = "获取标签列表")
    @GetMapping
    public Result<Page<TagResponse>> getTags(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "articleCount") String orderBy,
            @Parameter(description = "是否降序") @RequestParam(defaultValue = "true") Boolean desc) {
        return tagService.getTags(new Page<>(pageNum, pageSize), keyword, orderBy, desc);
    }

    @Operation(summary = "获取热门标签")
    @GetMapping("/hot")
    public Result<List<TagResponse>> getHotTags(
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "10") Integer limit) {
        return tagService.getHotTags(limit);
    }

    @Operation(summary = "获取标签下的文章")
    @GetMapping("/{id}/articles")
    public Result<Page<ArticleResponse>> getTagArticles(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return tagService.getTagArticles(id, new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "批量删除标签")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteTags(
            @Parameter(description = "标签ID列表") @RequestBody List<Long> ids) {
        return tagService.batchDeleteTags(ids);
    }

    @Operation(summary = "获取标签统计信息")
    @GetMapping("/stats")
    public Result<List<TagResponse>> getTagStats() {
        return tagService.getTagStats();
    }

    @Operation(summary = "合并标签")
    @PostMapping("/merge")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TagResponse> mergeTags(
            @Parameter(description = "源标签ID") @RequestParam Long sourceId,
            @Parameter(description = "目标标签ID") @RequestParam Long targetId) {
        return tagService.mergeTags(sourceId, targetId);
    }

    @Operation(summary = "获取相关标签")
    @GetMapping("/{id}/related")
    public Result<List<TagResponse>> getRelatedTags(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "5") Integer limit) {
        return tagService.getRelatedTags(id, limit);
    }
}
