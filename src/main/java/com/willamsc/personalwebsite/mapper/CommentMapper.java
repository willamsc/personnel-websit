package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.willamsc.personalwebsite.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 评论Mapper接口
 * 
 * @author william
 * @since 2024-11-25
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 获取文章的顶级评论
     * 
     * @param articleId 文章ID
     * @param page 分页参数
     * @return 评论列表
     */
    @Select("SELECT * FROM comment WHERE article_id = #{articleId} AND parent_id IS NULL " +
            "AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    IPage<Comment> findTopComments(@Param("articleId") Long articleId, IPage<Comment> page);

    /**
     * 获取评论的回复列表
     * 
     * @param parentId 父评论ID
     * @return 回复列表
     */
    @Select("SELECT * FROM comment WHERE parent_id = #{parentId} " +
            "AND status = 1 AND deleted = 0 ORDER BY create_time ASC")
    List<Comment> findByParentId(Long parentId);

    /**
     * 获取用户的评论列表
     * 
     * @param userId 用户ID
     * @param page 分页参数
     * @return 评论列表
     */
    @Select("SELECT * FROM comment WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Comment> findByUserId(@Param("userId") Long userId, IPage<Comment> page);

    /**
     * 增加评论点赞数
     * 
     * @param commentId 评论ID
     * @return 更新的行数
     */
    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{commentId}")
    int incrementLikeCount(Long commentId);

    /**
     * 更新文章的评论数
     * 
     * @param articleId 文章ID
     * @return 更新的行数
     */
    @Update("UPDATE article SET comment_count = (" +
            "SELECT COUNT(*) FROM comment " +
            "WHERE article_id = #{articleId} AND status = 1 AND deleted = 0" +
            ") WHERE id = #{articleId}")
    int updateArticleCommentCount(Long articleId);

    /**
     * 获取待审核的评论列表
     * 
     * @param page 分页参数
     * @return 评论列表
     */
    @Select("SELECT * FROM comment WHERE status = 0 AND deleted = 0 ORDER BY create_time ASC")
    IPage<Comment> findPendingComments(IPage<Comment> page);
}
