package com.willamsc.personalwebsite.security;

import com.willamsc.personalwebsite.entity.User;
import com.willamsc.personalwebsite.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * UserDetailsService实现类
 * 用于Spring Security从数据库加载用户信息
 * 
 * @author william
 * @since 2024-11-25
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    /**
     * 构造函数
     * 
     * @param userMapper 用户Mapper接口
     */
    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据用户名加载用户信息
     * 
     * @param username 用户名
     * @return UserDetails对象，包含用户认证和授权信息
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,  // 账号是否启用
                true,                   // 账号是否未过期
                true,                   // 凭证是否未过期
                true,                   // 账号是否未锁定
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
