package com.willamsc.personalwebsite.controller;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArchiveDTO;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.service.ArticleArchiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文章归档控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "文章归档", description = "文章归档相关接口")
@RestController
@RequestMapping("/api/v1/archives")
@RequiredArgsConstructor
public class ArticleArchiveController {

    private final ArticleArchiveService archiveService;

    @Operation(summary = "按月份归档")
    @GetMapping("/month")
    public Result<List<ArchiveDTO>> archiveByMonth() {
        return archiveService.archiveByMonth();
    }

    @Operation(summary = "按年份归档")
    @GetMapping("/year")
    public Result<List<ArchiveDTO>> archiveByYear() {
        return archiveService.archiveByYear();
    }

    @Operation(summary = "获取时间范围内的文章")
    @GetMapping("/range")
    public Result<List<ArticleResponse>> getArticlesByDateRange(
            @Parameter(description = "开始时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return archiveService.getArticlesByDateRange(startTime, endTime);
    }

    @Operation(summary = "获取文章时间线")
    @GetMapping("/timeline")
    public Result<Map<String, Integer>> getTimeline() {
        return archiveService.getTimeline();
    }
}
