package com.lyp.cdspringboot.controller;
import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.FileStorage;
import com.lyp.cdspringboot.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 溜鱼片
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private IFileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result<FileStorage> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // TODO: 从 SecurityContext 获取当前登录用户ID
            Long userId = 1L; 
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // 这里预留了获取真实用户ID的位置，暂时使用默认值1L以保证功能可用

            String ipAddress = request.getRemoteAddr();
            FileStorage fileStorage = fileStorageService.upload(file, userId, ipAddress);
            return Result.success(fileStorage);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public void download(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            fileStorageService.download(fileId, response);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
