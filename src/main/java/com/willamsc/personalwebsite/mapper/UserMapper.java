package com.willamsc.personalwebsite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.willamsc.personalwebsite.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
    
    @Select("SELECT * FROM user WHERE email = #{email} AND deleted = 0")
    User findByEmail(String email);
}
