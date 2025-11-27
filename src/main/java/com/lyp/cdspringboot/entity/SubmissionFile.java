package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 提交与文件关系表实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("submission_files")
public class SubmissionFile {

    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    @TableField("submission_id")
    private Long submissionId;

    @TableField("file_id")
    private Long fileId;

    @TableField("file_category")
    private String fileCategory;

    @TableField("file_order")
    private Integer fileOrder;

    @TableField("description")
    private String description;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
