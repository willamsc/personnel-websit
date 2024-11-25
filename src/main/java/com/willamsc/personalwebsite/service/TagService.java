package com.willamsc.personalwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.ArticleResponse;
import com.willamsc.personalwebsite.dto.TagRequest;
import com.willamsc.personalwebsite.dto.TagResponse;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.entity.Tag;
import com.willamsc.personalwebsite.mapper.ArticleMapper;
import com.willamsc.personalwebsite.mapper.ArticleTagMapper;
import com.willamsc.personalwebsite.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class TagService extends ServiceImpl<TagMapper, Tag> {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleService articleService;

    /**
     * 创建标签
     *
     * @param request 标签请求
     * @return 标签响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TagResponse> createTag(TagRequest request) {
        // 检查标签名称是否已存在
        if (checkNameExists(request.getName(), null)) {
            return Result.error("标签名称已存在");
        }

        // 创建标签
        Tag tag = new Tag();
        BeanUtils.copyProperties(request, tag);
        tag.setArticleCount(0);
        save(tag);

        return Result.success(convertToResponse(tag));
    }

    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param request 标签请求
     * @return 标签响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<TagResponse> updateTag(Long id, TagRequest request) {
        // 获取标签
        Tag tag = getById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }

        // 检查标签名称是否已存在
        if (checkNameExists(request.getName(), id)) {
            return Result.error("标签名称已存在");
        }

        // 更新标签
        BeanUtils.copyProperties(request, tag);
        updateById(tag);

        return Result.success(convertToResponse(tag));
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteTag(Long id) {
        // 获取标签
        Tag tag = getById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }

        // 检查是否有关联的文章
        if (tag.getArticleCount() > 0) {
            return Result.error("请先解除标签与文章的关联");
        }

        // 删除标签
        removeById(id);

        return Result.success();
    }

    /**
     * 获取标签列表
     *
     * @param page 分页参数
     * @return 标签响应列表
     */
    public Result<IPage<TagResponse>> getTags(Page<Tag> page) {
        // 分页查询标签
        IPage<Tag> tagPage = page(page, new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getArticleCount));

        // 转换为响应对象
        IPage<TagResponse> responsePage = tagPage.convert(this::convertToResponse);
        return Result.success(responsePage);
    }

    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return 标签响应
     */
    public Result<TagResponse> getTag(Long id) {
        // 获取标签
        Tag tag = getById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }

        // 转换为响应对象
        TagResponse response = convertToResponse(tag);

        // 获取相关文章
        IPage<Article> articlePage = articleMapper.findByTagId(id, new Page<>(1, 10));
        List<ArticleResponse> articles = articlePage.getRecords().stream()
                .map(articleService::convertToResponse)
                .collect(Collectors.toList());
        response.setArticles(articles);

        return Result.success(response);
    }

    /**
     * 获取热门标签
     *
     * @param limit 限制数量
     * @return 标签响应列表
     */
    public Result<List<TagResponse>> getHotTags(int limit) {
        // 获取热门标签
        List<Tag> tags = baseMapper.findHotTags(limit);

        // 转换为响应对象
        List<TagResponse> responses = tags.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    /**
     * 检查标签名称是否已存在
     *
     * @param name 标签名称
     * @param excludeId 排除的标签ID
     * @return 是否存在
     */
    private boolean checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, name);
        if (excludeId != null) {
            wrapper.ne(Tag::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * 将标签实体转换为响应DTO
     *
     * @param tag 标签实体
     * @return 标签响应DTO
     */
    private TagResponse convertToResponse(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagResponse response = new TagResponse();
        BeanUtils.copyProperties(tag, response);
        return response;
    }
}
