package com.willamsc.personalwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArchiveDTO;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章归档服务
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class ArticleArchiveService {

    private final ArticleService articleService;
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");

    /**
     * 按月份归档文章
     *
     * @return 归档结果
     */
    public Result<List<ArchiveDTO>> archiveByMonth() {
        // 获取所有已发布的文章
        List<Article> articles = articleService.list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreateTime));

        // 按月份分组
        Map<String, List<Article>> archiveMap = articles.stream()
                .collect(Collectors.groupingBy(
                        article -> article.getCreateTime().format(YEAR_MONTH_FORMATTER),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 构建归档结果
        List<ArchiveDTO> archives = new ArrayList<>();
        archiveMap.forEach((month, articleList) -> {
            ArchiveDTO archive = new ArchiveDTO();
            archive.setDate(month);
            archive.setCount(articleList.size());
            archive.setArticles(articleList.stream()
                    .map(articleService::convertToResponse)
                    .collect(Collectors.toList()));
            archives.add(archive);
        });

        return Result.success(archives);
    }

    /**
     * 按年份归档文章
     *
     * @return 归档结果
     */
    public Result<List<ArchiveDTO>> archiveByYear() {
        // 获取所有已发布的文章
        List<Article> articles = articleService.list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreateTime));

        // 按年份分组
        Map<String, List<Article>> archiveMap = articles.stream()
                .collect(Collectors.groupingBy(
                        article -> article.getCreateTime().format(YEAR_FORMATTER),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 构建归档结果
        List<ArchiveDTO> archives = new ArrayList<>();
        archiveMap.forEach((year, articleList) -> {
            ArchiveDTO archive = new ArchiveDTO();
            archive.setDate(year);
            archive.setCount(articleList.size());
            archive.setArticles(articleList.stream()
                    .map(articleService::convertToResponse)
                    .collect(Collectors.toList()));
            archives.add(archive);
        });

        return Result.success(archives);
    }

    /**
     * 获取指定时间范围内的文章
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 文章列表
     */
    public Result<List<ArticleResponse>> getArticlesByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<Article> articles = articleService.list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .between(Article::getCreateTime, startTime, endTime)
                .orderByDesc(Article::getCreateTime));

        List<ArticleResponse> responses = articles.stream()
                .map(articleService::convertToResponse)
                .collect(Collectors.toList());

        return Result.success(responses);
    }

    /**
     * 获取文章时间线
     *
     * @return 时间线数据
     */
    public Result<Map<String, Integer>> getTimeline() {
        List<Article> articles = articleService.list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreateTime));

        Map<String, Integer> timeline = articles.stream()
                .collect(Collectors.groupingBy(
                        article -> article.getCreateTime().format(YEAR_MONTH_FORMATTER),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), List::size)
                ));

        return Result.success(timeline);
    }
}
