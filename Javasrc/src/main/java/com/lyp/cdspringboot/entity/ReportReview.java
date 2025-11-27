package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 报告评审实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("report_reviews")
public class ReportReview {
    
    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;
    
    @TableField("submission_id")
    private Long submissionId;
    
    @TableField("reviewer_id")
    private Long reviewerId;
    
    @TableField("score")
    private Integer score;
    
    @TableField("feedback")
    private String feedback;
    
    @TableField("review_status")
    private String reviewStatus;
    
    @TableField("reviewed_at")
    private LocalDateTime reviewedAt;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 关联对象
    @TableField(exist = false)
    private ReportSubmission submission;
    
    @TableField(exist = false)
    private User reviewer;
}
