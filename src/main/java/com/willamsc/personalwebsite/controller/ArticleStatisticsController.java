package com.willamsc.personalwebsite.controller;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleStatisticsDTO;
import com.willamsc.personalwebsite.service.ArticleStatisticsService;
import com.willamsc.personalwebsite.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 文章统计控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "文章统计", description = "文章统计相关接口")
@RestController
@RequestMapping("/api/v1/articles/statistics")
@RequiredArgsConstructor
public class ArticleStatisticsController {

    private final ArticleStatisticsService articleStatisticsService;

    @Operation(summary = "增加文章浏览量")
    @PostMapping("/{articleId}/view")
    public Result<ArticleStatisticsDTO> incrementViewCount(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        return Result.success(articleStatisticsService.incrementViewCount(articleId));
    }

    @Operation(summary = "点赞或取消点赞文章")
    @PostMapping("/{articleId}/like")
    @PreAuthorize("isAuthenticated()")
    public Result<ArticleStatisticsDTO> toggleLike(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(articleStatisticsService.toggleLike(articleId, userId));
    }

    @Operation(summary = "收藏或取消收藏文章")
    @PostMapping("/{articleId}/favorite")
    @PreAuthorize("isAuthenticated()")
    public Result<ArticleStatisticsDTO> toggleFavorite(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(articleStatisticsService.toggleFavorite(articleId, userId));
    }

    @Operation(summary = "增加文章分享数")
    @PostMapping("/{articleId}/share")
    public Result<ArticleStatisticsDTO> incrementShareCount(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        return Result.success(articleStatisticsService.incrementShareCount(articleId));
    }

    @Operation(summary = "获取文章统计信息")
    @GetMapping("/{articleId}")
    public Result<ArticleStatisticsDTO> getArticleStatistics(
            @Parameter(description = "文章ID") @PathVariable Long articleId) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        return Result.success(articleStatisticsService.getArticleStatistics(articleId, userId));
    }
}
