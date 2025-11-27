package com.lyp.cdspringboot.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成密码 "123456" 的BCrypt加密值
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("新生成的加密密码: " + encodedPassword);
        
        // 验证新生成的密码
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("新密码验证: " + matches);
        
        // 验证我们之前生成的密码
        String ourPassword = "$2a$10$yraw5wTim1xbRZPL4NFc1ezADHCHNaSCe.h9uegaNQtBv7topEMWi";
        boolean ourMatches = encoder.matches(password, ourPassword);
        System.out.println("我们的密码验证: " + ourMatches);
        
        // 验证原数据库中的密码
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLyU7GhbwNPO";
        boolean dbMatches = encoder.matches(password, dbPassword);
        System.out.println("原数据库密码验证: " + dbMatches);
        
        // 生成多个密码进行测试
        System.out.println("\n=== 生成多个测试密码 ===");
        for (int i = 0; i < 3; i++) {
            String testPassword = encoder.encode(password);
            boolean testMatches = encoder.matches(password, testPassword);
            System.out.println("测试密码" + (i+1) + ": " + testPassword);
            System.out.println("验证结果" + (i+1) + ": " + testMatches);
        }
    }
}
