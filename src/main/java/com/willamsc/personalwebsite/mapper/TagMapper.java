package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willamsc.personalwebsite.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 文章标签Mapper接口
 * 
 * @author william
 * @since 2024-11-25
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 获取文章的所有标签
     * 
     * @param articleId 文章ID
     * @return 标签列表
     */
    @Select("SELECT t.* FROM tag t " +
            "INNER JOIN article_tag at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId} AND t.deleted = 0")
    List<Tag> findByArticleId(Long articleId);

    /**
     * 更新标签的文章数量
     * 
     * @param tagId 标签ID
     * @return 更新的行数
     */
    @Update("UPDATE tag SET article_count = (" +
            "SELECT COUNT(*) FROM article_tag at " +
            "INNER JOIN article a ON at.article_id = a.id " +
            "WHERE at.tag_id = #{tagId} AND a.deleted = 0" +
            ") WHERE id = #{tagId}")
    int updateArticleCount(Long tagId);

    /**
     * 获取热门标签
     * 
     * @param limit 限制数量
     * @return 标签列表
     */
    @Select("SELECT * FROM tag WHERE deleted = 0 ORDER BY article_count DESC LIMIT #{limit}")
    List<Tag> findHotTags(int limit);
}
