package com.lyp.cdspringboot.controller;

import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.dto.LoginRequest;
import com.lyp.cdspringboot.dto.LoginResponse;
import com.lyp.cdspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
           // System.out.println("收到登录请求: " + loginRequest.getUsername());
            LoginResponse response = userService.login(loginRequest);
           // System.out.println("登录成功: " + response.getUser().getUsername());
            return Result.success("登录成功", response);
        } catch (Exception e) {
          //  System.out.println("登录失败: " + e.getMessage());
         //   e.printStackTrace();
            return Result.error(e.getMessage());
        }

    }
    
    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("API连接正常");
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，客户端删除token即可
        return Result.success("登出成功");
    }
}
