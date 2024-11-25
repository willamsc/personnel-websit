package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.willamsc.personalwebsite.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 文章Mapper接口
 * 
 * @author william
 * @since 2024-11-25
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取指定分类的文章列表
     * 
     * @param categoryId 分类ID
     * @param page 分页参数
     * @return 文章列表
     */
    @Select("SELECT * FROM article WHERE category_id = #{categoryId} AND status = 1 AND deleted = 0 ORDER BY is_top DESC, create_time DESC")
    IPage<Article> findByCategoryId(@Param("categoryId") Long categoryId, IPage<Article> page);

    /**
     * 获取指定标签的文章列表
     * 
     * @param tagId 标签ID
     * @param page 分页参数
     * @return 文章列表
     */
    @Select("SELECT a.* FROM article a " +
            "INNER JOIN article_tag at ON a.id = at.article_id " +
            "WHERE at.tag_id = #{tagId} AND a.status = 1 AND a.deleted = 0 " +
            "ORDER BY a.is_top DESC, a.create_time DESC")
    IPage<Article> findByTagId(@Param("tagId") Long tagId, IPage<Article> page);

    /**
     * 获取指定作者的文章列表
     * 
     * @param authorId 作者ID
     * @param page 分页参数
     * @return 文章列表
     */
    @Select("SELECT * FROM article WHERE author_id = #{authorId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Article> findByAuthorId(@Param("authorId") Long authorId, IPage<Article> page);

    /**
     * 增加文章阅读量
     * 
     * @param articleId 文章ID
     * @return 更新的行数
     */
    @Update("UPDATE article SET view_count = view_count + 1 WHERE id = #{articleId}")
    int incrementViewCount(Long articleId);

    /**
     * 增加文章点赞数
     * 
     * @param articleId 文章ID
     * @return 更新的行数
     */
    @Update("UPDATE article SET like_count = like_count + 1 WHERE id = #{articleId}")
    int incrementLikeCount(Long articleId);

    /**
     * 获取热门文章
     * 
     * @param limit 限制数量
     * @return 文章列表
     */
    @Select("SELECT * FROM article WHERE status = 1 AND deleted = 0 " +
            "ORDER BY view_count DESC, like_count DESC LIMIT #{limit}")
    List<Article> findHotArticles(int limit);

    /**
     * 获取推荐文章
     * 
     * @param articleId 当前文章ID
     * @param limit 限制数量
     * @return 文章列表
     */
    @Select("SELECT a.* FROM article a " +
            "INNER JOIN article_tag at1 ON a.id = at1.article_id " +
            "INNER JOIN article_tag at2 ON at1.tag_id = at2.tag_id " +
            "WHERE at2.article_id = #{articleId} AND a.id != #{articleId} " +
            "AND a.status = 1 AND a.deleted = 0 " +
            "GROUP BY a.id " +
            "ORDER BY COUNT(*) DESC, a.view_count DESC " +
            "LIMIT #{limit}")
    List<Article> findRecommendArticles(@Param("articleId") Long articleId, @Param("limit") int limit);
}
