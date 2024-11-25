package com.willamsc.personalwebsite.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.service.ArticleService;
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
 * 文章控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "文章管理", description = "文章相关接口")
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request) {
        return articleService.createArticle(request);
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ArticleResponse> updateArticle(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request) {
        return articleService.updateArticle(id, request);
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        return articleService.deleteArticle(id);
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<ArticleResponse> getArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        return articleService.getArticle(id);
    }

    @Operation(summary = "获取文章列表")
    @GetMapping
    public Result<Page<ArticleResponse>> getArticles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "标签ID") @RequestParam(required = false) Long tagId,
            @Parameter(description = "作者ID") @RequestParam(required = false) Long authorId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String orderBy,
            @Parameter(description = "是否降序") @RequestParam(defaultValue = "true") Boolean desc) {
        return articleService.getArticles(pageNum, pageSize, categoryId, tagId, authorId, keyword, orderBy, desc);
    }

    @Operation(summary = "获取热门文章")
    @GetMapping("/hot")
    public Result<List<ArticleResponse>> getHotArticles(
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "10") Integer limit) {
        return articleService.getHotArticles(limit);
    }

    @Operation(summary = "获取推荐文章")
    @GetMapping("/recommended")
    public Result<List<ArticleResponse>> getRecommendedArticles(
            @Parameter(description = "文章ID") @RequestParam Long articleId,
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "5") Integer limit) {
        return articleService.getRecommendedArticles(articleId, limit);
    }

    @Operation(summary = "获取文章归档")
    @GetMapping("/archives")
    public Result<Page<ArticleResponse>> getArchives(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return articleService.getArchives(new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "点赞文章")
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        return articleService.likeArticle(id);
    }

    @Operation(summary = "更新文章状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ArticleResponse> updateArticleStatus(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Parameter(description = "状态：0草稿，1已发布，2已下架") @RequestParam Integer status) {
        return articleService.updateArticleStatus(id, status);
    }

    @Operation(summary = "获取草稿箱文章")
    @GetMapping("/drafts")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ArticleResponse>> getDrafts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return articleService.getDrafts(new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "置顶/取消置顶文章")
    @PutMapping("/{id}/top")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ArticleResponse> toggleTopArticle(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Parameter(description = "是否置顶") @RequestParam Boolean top) {
        return articleService.toggleTopArticle(id, top);
    }
}
