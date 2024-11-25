package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.document.ArticleDocument;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索服务
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ArticleRepository articleRepository;
    private final ArticleService articleService;

    /**
     * 同步文章到ES
     *
     * @param article 文章
     */
    public void syncArticle(Article article) {
        ArticleDocument document = new ArticleDocument();
        BeanUtils.copyProperties(article, document);
        // 设置分类名称
        if (article.getCategoryId() != null) {
            document.setCategoryName(articleService.getCategoryName(article.getCategoryId()));
        }
        // 设置标签
        if (article.getTagIds() != null && !article.getTagIds().isEmpty()) {
            document.setTagIds(article.getTagIds());
            document.setTags(articleService.getTagNames(article.getTagIds()));
        }
        articleRepository.save(document);
    }

    /**
     * 删除文章索引
     *
     * @param id 文章ID
     */
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    /**
     * 搜索文章
     *
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果
     */
    public Result<Page<ArticleResponse>> searchArticles(String keyword, int page, int size) {
        SearchHits<ArticleDocument> searchHits = articleRepository.search(keyword, PageRequest.of(page - 1, size));
        
        List<ArticleResponse> articles = searchHits.stream().map(this::convertToResponse).collect(Collectors.toList());
        return Result.success(new PageImpl<>(articles, PageRequest.of(page - 1, size), searchHits.getTotalHits()));
    }

    /**
     * 查询相关文章
     *
     * @param articleId 文章ID
     * @param categoryId 分类ID
     * @param tagIds 标签ID列表
     * @param limit 获取数量
     * @return 相关文章列表
     */
    public Result<List<ArticleResponse>> findRelatedArticles(Long articleId, Long categoryId, List<Long> tagIds, int limit) {
        Page<ArticleDocument> page = articleRepository.findRelated(categoryId, tagIds, articleId, PageRequest.of(0, limit));
        List<ArticleResponse> articles = page.getContent().stream()
                .map(doc -> {
                    ArticleResponse response = new ArticleResponse();
                    BeanUtils.copyProperties(doc, response);
                    return response;
                })
                .collect(Collectors.toList());
        return Result.success(articles);
    }

    private ArticleResponse convertToResponse(SearchHit<ArticleDocument> hit) {
        ArticleDocument document = hit.getContent();
        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(document, response);

        // 处理高亮
        Map<String, List<String>> highlightFields = hit.getHighlightFields();
        if (highlightFields.containsKey("title")) {
            response.setTitle(highlightFields.get("title").get(0));
        }
        if (highlightFields.containsKey("content")) {
            response.setContent(highlightFields.get("content").get(0));
        }

        return response;
    }
}
