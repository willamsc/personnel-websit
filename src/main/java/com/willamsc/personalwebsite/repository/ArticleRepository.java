package com.willamsc.personalwebsite.repository;

import com.willamsc.personalwebsite.document.ArticleDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章搜索仓库
 *
 * @author william
 * @since 2024-11-25
 */
@Repository
public interface ArticleRepository extends ElasticsearchRepository<ArticleDocument, Long> {

    /**
     * 搜索文章
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    @Highlight(fields = {
        @HighlightField(name = "title"),
        @HighlightField(name = "content")
    })
    @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}")
    SearchHits<ArticleDocument> search(String keyword, Pageable pageable);

    /**
     * 根据分类ID查询文章
     *
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 文章列表
     */
    Page<ArticleDocument> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 根据标签ID查询文章
     *
     * @param tagId 标签ID
     * @param pageable 分页参数
     * @return 文章列表
     */
    Page<ArticleDocument> findByTagIdsContaining(Long tagId, Pageable pageable);

    /**
     * 根据作者ID查询文章
     *
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 文章列表
     */
    Page<ArticleDocument> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * 查询热门文章
     *
     * @param status 状态
     * @param pageable 分页参数
     * @return 文章列表
     */
    Page<ArticleDocument> findByStatusOrderByViewCountDesc(Integer status, Pageable pageable);

    /**
     * 查询相关文章
     *
     * @param categoryId 分类ID
     * @param tagIds 标签ID列表
     * @param articleId 当前文章ID
     * @param pageable 分页参数
     * @return 文章列表
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"status\": 1}}, {\"bool\": {\"should\": [{\"term\": {\"categoryId\": ?0}}, {\"terms\": {\"tagIds\": ?1}}]}}], \"must_not\": [{\"term\": {\"id\": ?2}}]}}")
    Page<ArticleDocument> findRelated(Long categoryId, List<Long> tagIds, Long articleId, Pageable pageable);
}
