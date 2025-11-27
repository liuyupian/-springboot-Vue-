package com.lyp.cdspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyp.cdspringboot.entity.FileStorage;
import com.lyp.cdspringboot.mapper.FileStorageMapper;
import com.lyp.cdspringboot.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageServiceImpl extends ServiceImpl<FileStorageMapper, FileStorage> implements IFileStorageService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public FileStorage upload(MultipartFile file, Long userId, String ipAddress) throws IOException {
        // 1. 检查目录是否存在
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. 获取文件基本信息
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        
        // 3. 保存文件到本地
        File targetFile = new File(directory, newFileName);
        file.transferTo(targetFile);

        // 4. 计算文件哈希
        String fileHash;
        try (FileInputStream fis = new FileInputStream(targetFile)) {
            fileHash = DigestUtils.md5DigestAsHex(fis);
        }

        // 5. 保存信息到数据库
        FileStorage fileStorage = new FileStorage();
        fileStorage.setFileName(originalFilename);
        fileStorage.setFilePath(targetFile.getAbsolutePath());
        fileStorage.setFileSize(file.getSize());
        fileStorage.setFileType(suffix.substring(1)); // 去掉点
        fileStorage.setMimeType(file.getContentType());
        fileStorage.setFileHash(fileHash);
        fileStorage.setStorageType(FileStorage.StorageType.LOCAL);
        fileStorage.setUploadBy(userId);
        fileStorage.setUploadIp(ipAddress);
        fileStorage.setIsDeleted(false);
        fileStorage.setCreatedAt(LocalDateTime.now());

        this.save(fileStorage);
        return fileStorage;
    }

    @Override
    public void download(Long fileId, HttpServletResponse response) throws IOException {
        FileStorage fileStorage = this.getById(fileId);
        if (fileStorage == null || fileStorage.getIsDeleted()) {
            throw new RuntimeException("文件不存在或已删除");
        }

        File file = new File(fileStorage.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("文件物理路径不存在");
        }

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + 
                URLEncoder.encode(fileStorage.getFileName(), StandardCharsets.UTF_8.toString()));
        response.setContentLengthLong(file.length());

        // 写入流
        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }
}
