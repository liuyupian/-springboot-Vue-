package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户基础信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {
    
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @TableField("username")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @TableField("password")
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @TableField("real_name")
    private String realName;
    
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    private String email;
    
    @TableField("phone")
    private String phone;
    
    @TableField("user_type")
    private UserType userType;
    
    @TableField("status")
    private UserStatus status;
    
    @TableField("avatar_url")
    private String avatarUrl;
    
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 用户类型枚举
     */
    public enum UserType {
        STUDENT, TEACHER, ADMIN
    }
    
    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}
