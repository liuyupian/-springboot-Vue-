package com.lyp.cdspringboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyp.cdspringboot.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IFileStorageService extends IService<FileStorage> {
    /**
     * 上传文件
     * @param file 文件对象
     * @param userId 上传用户ID
     * @param ipAddress 上传IP地址
     * @return 文件存储信息
     */
    FileStorage upload(MultipartFile file, Long userId, String ipAddress) throws IOException;

    /**
     * 下载文件
     * @param fileId 文件ID
     * @param response 响应对象
     */
    void download(Long fileId, HttpServletResponse response) throws IOException;
}
