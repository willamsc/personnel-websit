package com.willamsc.personalwebsite.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.CommentRequest;
import com.willamsc.personalwebsite.dto.CommentResponse;
import com.willamsc.personalwebsite.entity.Comment;
import com.willamsc.personalwebsite.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 *
 * @author william
 * @since 2024-11-25
 */
@Tag(name = "评论管理", description = "评论相关接口")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    public Result<CommentResponse> createComment(@Valid @RequestBody CommentRequest request) {
        return commentService.createComment(request);
    }

    @Operation(summary = "更新评论状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<CommentResponse> updateCommentStatus(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @Parameter(description = "状态：0待审核，1通过，2拒绝") @RequestParam Integer status) {
        return commentService.updateCommentStatus(id, status);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        return commentService.deleteComment(id);
    }

    @Operation(summary = "获取文章评论列表")
    @GetMapping("/article/{articleId}")
    public Result<Page<CommentResponse>> getArticleComments(
            @Parameter(description = "文章ID") @PathVariable Long articleId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return commentService.getArticleComments(articleId, new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "获取用户评论列表")
    @GetMapping("/user/{userId}")
    public Result<Page<CommentResponse>> getUserComments(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return commentService.getUserComments(userId, new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "获取待审核评论列表")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<CommentResponse>> getPendingComments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return commentService.getPendingComments(new Page<>(pageNum, pageSize));
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(@Parameter(description = "评论ID") @PathVariable Long id) {
        return commentService.likeComment(id);
    }
}
