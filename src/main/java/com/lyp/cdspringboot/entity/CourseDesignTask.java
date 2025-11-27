package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程设计任务实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_design_tasks")
public class CourseDesignTask {
    
    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;
    
    @TableField("course_id")
    private Long courseId;
    
    // 暂时注释掉teacher_id，使用created_by代替
    // @TableField("teacher_id")
    // private Long teacherId;
    
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题长度不能超过200个字符")
    @TableField("task_title")
    private String taskTitle;
    
    @TableField("task_description")
    private String taskDescription;
    
    @TableField("requirements")
    private String requirements;
    
    @TableField("start_date")
    private LocalDate startDate;
    
    @TableField("due_date")
    private LocalDate dueDate;
    
    @TableField("max_score")
    private Integer maxScore;
    
    @TableField("allowed_file_types")
    private String allowedFileTypes;
    
    @TableField("max_file_size")
    private Long maxFileSize;
    
    @TableField("max_file_count")
    private Integer maxFileCount;
    
    @TableField("allow_late_submission")
    private Boolean allowLateSubmission;
    
    @TableField("late_penalty_rate")
    private BigDecimal latePenaltyRate;
    
    @TableField("status")
    private TaskStatus status;
    
    @TableField("created_by")
    private Long createdBy;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 关联对象
    @TableField(exist = false)
    private Course course;
    
    @TableField(exist = false)
    private Teacher creator;
    
    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        DRAFT, PUBLISHED, CLOSED
    }
}
