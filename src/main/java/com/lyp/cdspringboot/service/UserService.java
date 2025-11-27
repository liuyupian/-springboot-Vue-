package com.lyp.cdspringboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyp.cdspringboot.dto.LoginRequest;
import com.lyp.cdspringboot.dto.LoginResponse;
import com.lyp.cdspringboot.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 创建用户
     */
    User createUser(User user);
    
    /**
     * 更新用户信息
     */
    User updateUser(User user);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);
}
