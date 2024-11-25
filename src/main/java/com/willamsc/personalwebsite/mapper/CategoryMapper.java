package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willamsc.personalwebsite.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章分类Mapper接口
 * 
 * @author william
 * @since 2024-11-25
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 获取所有顶级分类
     * 
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id IS NULL AND deleted = 0 ORDER BY sort")
    List<Category> findTopCategories();

    /**
     * 获取指定分类的子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort")
    List<Category> findByParentId(Long parentId);
}
