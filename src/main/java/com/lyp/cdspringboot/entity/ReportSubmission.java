package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报告提交实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("report_submissions")
public class ReportSubmission {
    
    @TableId(value = "submission_id", type = IdType.AUTO)
    private Long submissionId;
    
    @TableField("task_id")
    private Long taskId;
    
    @TableField("student_id")
    private Long studentId;
    
    @NotBlank(message = "报告标题不能为空")
    @Size(max = 200, message = "报告标题长度不能超过200个字符")
    @TableField("title")
    private String title;
    
    @TableField("description")
    private String description;
    
    @TableField("submission_count")
    private Integer submissionCount;
    
    @TableField("is_late")
    private Boolean isLate;
    
    @TableField("late_days")
    private Integer lateDays;
    
    @TableField("status")
    private SubmissionStatus status;
    
    @TableField("submitted_at")
    private LocalDateTime submittedAt;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 关联对象
    @TableField(exist = false)
    private CourseDesignTask task;
    
    @TableField(exist = false)
    private Student student;

    // 展示用字段
    @TableField(exist = false)
    private String taskTitle;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String studentName;

    // 前端提交时携带的文件ID列表
    @TableField(exist = false)
    private List<Long> files;

    // 前端展示用：文件详细信息列表
    @TableField(exist = false)
    private List<Map<String, Object>> fileInfos;
    
    // 前端展示用：评审信息（来自 report_reviews 表）
    @TableField(exist = false)
    private Integer score;
    
    @TableField(exist = false)
    private String feedback;
    
    @TableField(exist = false)
    private LocalDateTime reviewedAt;
    
    /**
     * 提交状态枚举
     */
    public enum SubmissionStatus {
        DRAFT, SUBMITTED, UNDER_REVIEW, PASS, FAIL, REDO
    }
}
