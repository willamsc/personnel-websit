package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willamsc.personalwebsite.entity.ArticleStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章统计Mapper接口
 *
 * @author william
 * @since 2024-11-25
 */
@Mapper
public interface ArticleStatisticsMapper extends BaseMapper<ArticleStatistics> {
}
