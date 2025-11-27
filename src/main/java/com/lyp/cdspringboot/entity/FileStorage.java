package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 文件存储实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_storage")
public class FileStorage {
    
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;
    
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    @TableField("file_name")
    private String fileName;
    
    @NotBlank(message = "文件路径不能为空")
    @Size(max = 500, message = "文件路径长度不能超过500个字符")
    @TableField("file_path")
    private String filePath;
    
    @TableField("file_size")
    private Long fileSize;
    
    @TableField("file_type")
    private String fileType;
    
    @TableField("mime_type")
    private String mimeType;
    
    @TableField("file_hash")
    private String fileHash;
    
    @TableField("storage_type")
    private StorageType storageType;
    
    @TableField("upload_by")
    private Long uploadBy;
    
    @TableField("upload_ip")
    private String uploadIp;
    
    @TableField("is_deleted")
    private Boolean isDeleted;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 关联对象
    @TableField(exist = false)
    private User uploader;
    
    /**
     * 存储类型枚举
     */
    public enum StorageType {
        LOCAL, OSS, COS, S3
    }
}
