package com.willamsc.personalwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.CategoryRequest;
import com.willamsc.personalwebsite.dto.CategoryResponse;
import com.willamsc.personalwebsite.entity.Category;
import com.willamsc.personalwebsite.mapper.ArticleMapper;
import com.willamsc.personalwebsite.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    private final ArticleMapper articleMapper;

    /**
     * 创建分类
     *
     * @param request 分类请求
     * @return 分类响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<CategoryResponse> createCategory(CategoryRequest request) {
        // 检查分类名称是否已存在
        if (checkNameExists(request.getName(), null)) {
            return Result.error("分类名称已存在");
        }

        // 如果有父分类，检查父分类是否存在
        if (request.getParentId() != null) {
            Category parent = getById(request.getParentId());
            if (parent == null) {
                return Result.error("父分类不存在");
            }
        }

        // 创建分类
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        save(category);

        return Result.success(convertToResponse(category));
    }

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param request 分类请求
     * @return 分类响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<CategoryResponse> updateCategory(Long id, CategoryRequest request) {
        // 获取分类
        Category category = getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }

        // 检查分类名称是否已存在
        if (checkNameExists(request.getName(), id)) {
            return Result.error("分类名称已存在");
        }

        // 如果有父分类，检查父分类是否存在，并且不能设置自己或子分类为父分类
        if (request.getParentId() != null) {
            if (id.equals(request.getParentId())) {
                return Result.error("不能设置自己为父分类");
            }
            Category parent = getById(request.getParentId());
            if (parent == null) {
                return Result.error("父分类不存在");
            }
            if (isChild(id, request.getParentId())) {
                return Result.error("不能设置子分类为父分类");
            }
        }

        // 更新分类
        BeanUtils.copyProperties(request, category);
        updateById(category);

        return Result.success(convertToResponse(category));
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteCategory(Long id) {
        // 获取分类
        Category category = getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }

        // 检查是否有子分类
        List<Category> children = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id));
        if (!children.isEmpty()) {
            return Result.error("请先删除子分类");
        }

        // 检查是否有关联的文章
        Long articleCount = articleMapper.selectCount(new LambdaQueryWrapper<>()
                .eq("category_id", id));
        if (articleCount > 0) {
            return Result.error("请先删除分类下的文章");
        }

        // 删除分类
        removeById(id);

        return Result.success();
    }

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    public Result<List<CategoryResponse>> getCategoryTree() {
        // 获取所有分类
        List<Category> categories = list(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort));

        // 构建分类树
        List<CategoryResponse> tree = buildCategoryTree(categories);

        return Result.success(tree);
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类响应
     */
    public Result<CategoryResponse> getCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }

        return Result.success(convertToResponse(category));
    }

    /**
     * 检查分类名称是否已存在
     *
     * @param name 分类名称
     * @param excludeId 排除的分类ID
     * @return 是否存在
     */
    private boolean checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * 检查是否是子分类
     *
     * @param parentId 父分类ID
     * @param childId 子分类ID
     * @return 是否是子分类
     */
    private boolean isChild(Long parentId, Long childId) {
        Category child = getById(childId);
        if (child == null || child.getParentId() == null) {
            return false;
        }
        if (child.getParentId().equals(parentId)) {
            return true;
        }
        return isChild(parentId, child.getParentId());
    }

    /**
     * 构建分类树
     *
     * @param categories 分类列表
     * @return 分类树
     */
    private List<CategoryResponse> buildCategoryTree(List<Category> categories) {
        // 转换为响应对象
        List<CategoryResponse> responses = categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 构建父子关系
        Map<Long, List<CategoryResponse>> childrenMap = responses.stream()
                .filter(category -> category.getParentId() != null)
                .collect(Collectors.groupingBy(CategoryResponse::getParentId));

        // 设置子分类
        responses.forEach(category -> 
            category.setChildren(childrenMap.getOrDefault(category.getId(), new ArrayList<>())));

        // 返回顶级分类
        return responses.stream()
                .filter(category -> category.getParentId() == null)
                .collect(Collectors.toList());
    }

    /**
     * 将分类实体转换为响应DTO
     *
     * @param category 分类实体
     * @return 分类响应DTO
     */
    private CategoryResponse convertToResponse(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);

        // 设置父分类名称
        if (category.getParentId() != null) {
            Category parent = getById(category.getParentId());
            if (parent != null) {
                response.setParentName(parent.getName());
            }
        }

        // 设置文章数量
        Long articleCount = articleMapper.selectCount(new LambdaQueryWrapper<>()
                .eq("category_id", category.getId()));
        response.setArticleCount(articleCount.intValue());

        return response;
    }
}
