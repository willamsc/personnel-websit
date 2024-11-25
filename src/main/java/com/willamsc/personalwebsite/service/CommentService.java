package com.willamsc.personalwebsite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.willamsc.personalwebsite.common.Result;
import com.willamsc.personalwebsite.dto.CommentRequest;
import com.willamsc.personalwebsite.dto.CommentResponse;
import com.willamsc.personalwebsite.entity.Article;
import com.willamsc.personalwebsite.entity.Comment;
import com.willamsc.personalwebsite.entity.User;
import com.willamsc.personalwebsite.mapper.ArticleMapper;
import com.willamsc.personalwebsite.mapper.CommentMapper;
import com.willamsc.personalwebsite.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author william
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
public class CommentService extends ServiceImpl<CommentMapper, Comment> {

    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    /**
     * 创建评论
     *
     * @param request 评论请求
     * @return 评论响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<CommentResponse> createComment(CommentRequest request) {
        // 获取当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 检查文章是否存在
        Article article = articleMapper.selectById(request.getArticleId());
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 如果是回复评论，检查父评论是否存在
        if (request.getParentId() != null) {
            Comment parent = getById(request.getParentId());
            if (parent == null) {
                return Result.error("父评论不存在");
            }
            // 如果父评论是子评论，则设置为父评论的父评论
            if (parent.getParentId() != null) {
                request.setParentId(parent.getParentId());
                request.setReplyUserId(parent.getUserId());
            }
        }

        // 创建评论
        Comment comment = new Comment();
        BeanUtils.copyProperties(request, comment);
        comment.setUserId(user.getId());
        comment.setLikeCount(0);
        comment.setStatus(0); // 待审核
        save(comment);

        // 更新文章评论数
        baseMapper.updateArticleCommentCount(request.getArticleId());

        return Result.success(convertToResponse(comment));
    }

    /**
     * 更新评论状态
     *
     * @param id 评论ID
     * @param status 状态
     * @return 评论响应
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<CommentResponse> updateCommentStatus(Long id, Integer status) {
        // 获取评论
        Comment comment = getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 更新状态
        comment.setStatus(status);
        updateById(comment);

        // 更新文章评论数
        baseMapper.updateArticleCommentCount(comment.getArticleId());

        return Result.success(convertToResponse(comment));
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteComment(Long id) {
        // 获取评论
        Comment comment = getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 删除评论及其子评论
        remove(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getId, id)
                .or()
                .eq(Comment::getParentId, id));

        // 更新文章评论数
        baseMapper.updateArticleCommentCount(comment.getArticleId());

        return Result.success();
    }

    /**
     * 获取文章评论列表
     *
     * @param articleId 文章ID
     * @param page 分页参数
     * @return 评论响应列表
     */
    public Result<IPage<CommentResponse>> getArticleComments(Long articleId, Page<Comment> page) {
        // 获取顶级评论
        IPage<Comment> commentPage = baseMapper.findTopComments(articleId, page);

        // 获取所有子评论
        List<Comment> allComments = new ArrayList<>(commentPage.getRecords());
        for (Comment comment : commentPage.getRecords()) {
            List<Comment> children = baseMapper.findByParentId(comment.getId());
            allComments.addAll(children);
        }

        // 构建评论树
        IPage<CommentResponse> responsePage = commentPage.convert(this::convertToResponse);
        buildCommentTree(responsePage.getRecords(), allComments);

        return Result.success(responsePage);
    }

    /**
     * 获取用户评论列表
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 评论响应列表
     */
    public Result<IPage<CommentResponse>> getUserComments(Long userId, Page<Comment> page) {
        // 获取用户评论
        IPage<Comment> commentPage = baseMapper.findByUserId(userId, page);

        // 转换为响应对象
        IPage<CommentResponse> responsePage = commentPage.convert(this::convertToResponse);
        return Result.success(responsePage);
    }

    /**
     * 获取待审核评论列表
     *
     * @param page 分页参数
     * @return 评论响应列表
     */
    public Result<IPage<CommentResponse>> getPendingComments(Page<Comment> page) {
        // 获取待审核评论
        IPage<Comment> commentPage = baseMapper.findPendingComments(page);

        // 转换为响应对象
        IPage<CommentResponse> responsePage = commentPage.convert(this::convertToResponse);
        return Result.success(responsePage);
    }

    /**
     * 点赞评论
     *
     * @param id 评论ID
     * @return 操作结果
     */
    public Result<Void> likeComment(Long id) {
        // 获取评论
        Comment comment = getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        // 增加点赞数
        baseMapper.incrementLikeCount(id);

        return Result.success();
    }

    /**
     * 构建评论树
     *
     * @param topComments 顶级评论
     * @param allComments 所有评论
     */
    private void buildCommentTree(List<CommentResponse> topComments, List<Comment> allComments) {
        // 转换所有评论为响应对象
        List<CommentResponse> allResponses = allComments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 构建父子关系
        Map<Long, List<CommentResponse>> childrenMap = allResponses.stream()
                .filter(comment -> comment.getParentId() != null)
                .collect(Collectors.groupingBy(CommentResponse::getParentId));

        // 设置子评论
        topComments.forEach(comment ->
            comment.setChildren(childrenMap.getOrDefault(comment.getId(), new ArrayList<>())));
    }

    /**
     * 将评论实体转换为响应DTO
     *
     * @param comment 评论实体
     * @return 评论响应DTO
     */
    private CommentResponse convertToResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponse response = new CommentResponse();
        BeanUtils.copyProperties(comment, response);

        // 设置文章信息
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article != null) {
            response.setArticleTitle(article.getTitle());
        }

        // 设置用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setUserAvatar(user.getAvatar());
        }

        // 设置父评论用户名
        if (comment.getParentId() != null) {
            Comment parent = getById(comment.getParentId());
            if (parent != null) {
                User parentUser = userMapper.selectById(parent.getUserId());
                if (parentUser != null) {
                    response.setParentUsername(parentUser.getUsername());
                }
            }
        }

        // 设置回复用户名
        if (comment.getReplyUserId() != null) {
            User replyUser = userMapper.selectById(comment.getReplyUserId());
            if (replyUser != null) {
                response.setReplyUsername(replyUser.getUsername());
            }
        }

        return response;
    }
}
