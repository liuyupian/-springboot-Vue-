package com.lyp.cdspringboot.dto;

import com.lyp.cdspringboot.entity.User;
import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    
    private String token;
    private User user;
    
    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
        // 清除敏感信息
        this.user.setPassword(null);
    }
}
