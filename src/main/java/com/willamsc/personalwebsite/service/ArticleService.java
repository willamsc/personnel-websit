package com.willamsc.personalwebsite.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleRequest;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.entity.ArticleTag;
import com.willamsc.personalwebsite.entity.Category;
import com.willamsc.personalwebsite.entity.Tag;
import com.willamsc.personalwebsite.mapper.ArticleMapper;
import com.willamsc.personalwebsite.mapper.ArticleTagMapper;
import com.willamsc.personalwebsite.mapper.CategoryMapper;
import com.willamsc.personalwebsite.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    /**
     * 创建文章
     *
     * @param request 文章请求
     * @return 文章响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<ArticleResponse> createArticle(ArticleRequest request) {
        // 获取当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 创建文章
        Article article = new Article();
        BeanUtils.copyProperties(request, article);
        article.setAuthorId(1L); // TODO: 从用户服务获取用户ID

        // 保存文章
        save(article);

        // 保存文章标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<ArticleTag> articleTags = request.getTagIds().stream()
                    .map(tagId -> {
                        ArticleTag articleTag = new ArticleTag();
                        articleTag.setArticleId(article.getId());
                        articleTag.setTagId(tagId);
                        return articleTag;
                    })
                    .collect(Collectors.toList());
            articleTagMapper.insertBatch(articleTags);

            // 更新标签文章数
            tagMapper.incrementArticleCount(request.getTagIds());
        }

        return Result.success(convertToResponse(article));
    }

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param request 文章请求
     * @return 文章响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<ArticleResponse> updateArticle(Long id, ArticleRequest request) {
        // 获取文章
        Article article = getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 更新文章基本信息
        BeanUtils.copyProperties(request, article);
        updateById(article);

        // 更新文章标签关联
        if (request.getTagIds() != null) {
            // 删除原有标签关联
            articleTagMapper.deleteByArticleId(id);

            // 添加新的标签关联
            if (!request.getTagIds().isEmpty()) {
                List<ArticleTag> articleTags = request.getTagIds().stream()
                        .map(tagId -> {
                            ArticleTag articleTag = new ArticleTag();
                            articleTag.setArticleId(id);
                            articleTag.setTagId(tagId);
                            return articleTag;
                        })
                        .collect(Collectors.toList());
                articleTagMapper.insertBatch(articleTags);
            }

            // 更新标签文章数
            tagMapper.updateArticleCount();
        }

        return Result.success(convertToResponse(article));
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteArticle(Long id) {
        // 获取文章
        Article article = getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 删除文章标签关联
        articleTagMapper.deleteByArticleId(id);

        // 删除文章
        removeById(id);

        // 更新标签文章数
        tagMapper.updateArticleCount();

        return Result.success();
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章响应
     */
    public Result<ArticleResponse> getArticle(Long id) {
        Article article = getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 增加阅读量
        baseMapper.incrementViewCount(id);

        return Result.success(convertToResponse(article));
    }

    /**
     * 分页查询文章列表
     *
     * @param page 分页参数
     * @return 文章响应列表
     */
    public Result<IPage<ArticleResponse>> getArticles(Page<Article> page) {
        IPage<Article> articlePage = page(page);
        
        IPage<ArticleResponse> responsePage = articlePage.convert(this::convertToResponse);
        return Result.success(responsePage);
    }

    /**
     * 根据分类ID分页查询文章列表
     *
     * @param categoryId 分类ID
     * @param page 分页参数
     * @return 文章响应列表
     */
    public Result<IPage<ArticleResponse>> getArticlesByCategory(Long categoryId, Page<Article> page) {
        IPage<Article> articlePage = baseMapper.findByCategoryId(categoryId, page);
        
        IPage<ArticleResponse> responsePage = articlePage.convert(this::convertToResponse);
        return Result.success(responsePage);
    }

    /**
     * 根据标签ID分页查询文章列表
     *
     * @param tagId 标签ID
     * @param page 分页参数
     * @return 文章响应列表
     */
    public Result<IPage<ArticleResponse>> getArticlesByTag(Long tagId, Page<Article> page) {
        IPage<Article> articlePage = baseMapper.findByTagId(tagId, page);
        
        IPage<ArticleResponse> responsePage = articlePage.convert(this::convertToResponse);
        return Result.success(responsePage);
    }

    /**
     * 获取热门文章
     *
     * @param limit 限制数量
     * @return 文章响应列表
     */
    public Result<List<ArticleResponse>> getHotArticles(int limit) {
        List<Article> articles = baseMapper.findHotArticles(limit);
        
        List<ArticleResponse> responses = articles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    /**
     * 获取推荐文章
     *
     * @param articleId 当前文章ID
     * @param limit 限制数量
     * @return 文章响应列表
     */
    public Result<List<ArticleResponse>> getRecommendArticles(Long articleId, int limit) {
        List<Article> articles = baseMapper.findRecommendArticles(articleId, limit);
        
        List<ArticleResponse> responses = articles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    /**
     * 点赞文章
     *
     * @param id 文章ID
     * @return 操作结果
     */
    public Result<Void> likeArticle(Long id) {
        // 获取文章
        Article article = getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 增加点赞数
        baseMapper.incrementLikeCount(id);

        return Result.success();
    }

    /**
     * 将文章实体转换为响应DTO
     *
     * @param article 文章实体
     * @return 文章响应DTO
     */
    private ArticleResponse convertToResponse(Article article) {
        if (article == null) {
            return null;
        }

        ArticleResponse response = new ArticleResponse();
        BeanUtils.copyProperties(article, response);

        // 设置分类信息
        Category category = categoryMapper.selectById(article.getCategoryId());
        response.setCategory(category);

        // 设置标签信息
        List<Tag> tags = tagMapper.findByArticleId(article.getId());
        response.setTags(tags);

        return response;
    }
}
