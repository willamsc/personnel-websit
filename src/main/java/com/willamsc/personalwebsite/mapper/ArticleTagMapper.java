package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willamsc.personalwebsite.entity.ArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章标签关联Mapper接口
 * 
 * @author william
 * @since 2024-11-25
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 删除文章的所有标签关联
     * 
     * @param articleId 文章ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(Long articleId);

    /**
     * 删除标签的所有文章关联
     * 
     * @param tagId 标签ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM article_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(Long tagId);

    /**
     * 删除指定的文章标签关联
     * 
     * @param articleId 文章ID
     * @param tagId 标签ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId} AND tag_id = #{tagId}")
    int deleteArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);
}
