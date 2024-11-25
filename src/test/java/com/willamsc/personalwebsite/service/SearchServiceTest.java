package com.willamsc.personalwebsite.service;

import com.willamsc.personalwebsite.BaseTest;
import com.willamsc.personalwebsite.document.ArticleDocument;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 搜索服务测试类
 *
 * @author william
 * @since 2024-11-25
 */
class SearchServiceTest extends BaseTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private ArticleRequest createArticleRequest;

    @BeforeEach
    void setUp() {
        createArticleRequest = new ArticleRequest();
        createArticleRequest.setTitle("Elasticsearch Guide");
        createArticleRequest.setContent("This is a comprehensive guide about Elasticsearch and its features.");
        createArticleRequest.setSummary("Learn about Elasticsearch");
        createArticleRequest.setCategoryId(1L);
        createArticleRequest.setTags("elasticsearch,search,guide");
        
        // 清理测试索引
        elasticsearchOperations.indexOps(ArticleDocument.class).delete();
        elasticsearchOperations.indexOps(ArticleDocument.class).create();
    }

    @Test
    void indexArticle() {
        // 创建文章
        ArticleResponse article = articleService.createArticle(createArticleRequest);
        
        // 索引文章
        searchService.indexArticle(article.getId());
        
        // 验证索引是否成功
        ArticleDocument indexed = elasticsearchOperations.get(article.getId().toString(), ArticleDocument.class);
        
        assertNotNull(indexed);
        assertEquals(article.getId(), indexed.getId());
        assertEquals(article.getTitle(), indexed.getTitle());
    }

    @Test
    void searchArticles() {
        // 创建并索引文章
        ArticleResponse article = articleService.createArticle(createArticleRequest);
        searchService.indexArticle(article.getId());
        
        // 搜索文章
        Page<ArticleResponse> results = searchService.searchArticles("elasticsearch", PageRequest.of(0, 10));
        
        assertNotNull(results);
        assertTrue(results.getTotalElements() > 0);
        assertEquals(article.getId(), results.getContent().get(0).getId());
    }

    @Test
    void searchArticlesWithNoResults() {
        // 创建并索引文章
        ArticleResponse article = articleService.createArticle(createArticleRequest);
        searchService.indexArticle(article.getId());
        
        // 搜索不存在的内容
        Page<ArticleResponse> results = searchService.searchArticles("nonexistent", PageRequest.of(0, 10));
        
        assertNotNull(results);
        assertEquals(0, results.getTotalElements());
    }

    @Test
    void getRelatedArticles() {
        // 创建并索引多篇文章
        ArticleResponse article1 = articleService.createArticle(createArticleRequest);
        searchService.indexArticle(article1.getId());
        
        createArticleRequest.setTitle("Another Elasticsearch Tutorial");
        createArticleRequest.setContent("More content about Elasticsearch and searching.");
        ArticleResponse article2 = articleService.createArticle(createArticleRequest);
        searchService.indexArticle(article2.getId());
        
        // 获取相关文章
        List<ArticleResponse> related = searchService.getRelatedArticles(article1.getId());
        
        assertNotNull(related);
        assertFalse(related.isEmpty());
        assertTrue(related.stream().anyMatch(a -> a.getId().equals(article2.getId())));
    }

    @Test
    void deleteArticleIndex() {
        // 创建并索引文章
        ArticleResponse article = articleService.createArticle(createArticleRequest);
        searchService.indexArticle(article.getId());
        
        // 删除索引
        searchService.deleteArticleIndex(article.getId());
        
        // 验证索引已删除
        ArticleDocument deleted = elasticsearchOperations.get(article.getId().toString(), ArticleDocument.class);
        assertNull(deleted);
    }

    @Test
    void reindexAllArticles() {
        // 创建多篇文章
        articleService.createArticle(createArticleRequest);
        
        createArticleRequest.setTitle("Second Article");
        articleService.createArticle(createArticleRequest);
        
        // 重建索引
        searchService.reindexAllArticles();
        
        // 验证所有文章都已被索引
        long indexedCount = elasticsearchOperations.count(null, ArticleDocument.class);
        assertTrue(indexedCount >= 2);
    }
}
