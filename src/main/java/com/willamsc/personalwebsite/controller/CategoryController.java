package com.willamsc.personalwebsite.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.CategoryRequest;
import com.willamsc.personalwebsite.dto.CategoryResponse;
import com.willamsc.personalwebsite.service.CategoryService;
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
 * 分类控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "分类管理", description = "分类相关接口")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<CategoryResponse> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<CategoryResponse> getCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @Operation(summary = "获取分类列表")
    @GetMapping
    public Result<Page<CategoryResponse>> getCategories(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "父分类ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        return categoryService.getCategories(pageNum, pageSize, parentId, keyword);
    }

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<CategoryResponse>> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @Operation(summary = "获取热门分类")
    @GetMapping("/hot")
    public Result<List<CategoryResponse>> getHotCategories(
            @Parameter(description = "获取数量") @RequestParam(defaultValue = "10") Integer limit) {
        return categoryService.getHotCategories(limit);
    }

    @Operation(summary = "更新分类顺序")
    @PutMapping("/{id}/order")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<CategoryResponse> updateCategoryOrder(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "顺序") @RequestParam Integer order) {
        return categoryService.updateCategoryOrder(id, order);
    }

    @Operation(summary = "获取分类路径")
    @GetMapping("/{id}/path")
    public Result<List<CategoryResponse>> getCategoryPath(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        return categoryService.getCategoryPath(id);
    }

    @Operation(summary = "获取子分类")
    @GetMapping("/{id}/children")
    public Result<List<CategoryResponse>> getChildCategories(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "是否递归") @RequestParam(defaultValue = "false") Boolean recursive) {
        return categoryService.getChildCategories(id, recursive);
    }

    @Operation(summary = "获取分类统计信息")
    @GetMapping("/stats")
    public Result<List<CategoryResponse>> getCategoryStats() {
        return categoryService.getCategoryStats();
    }
}
