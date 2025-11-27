package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("courses")
public class Course {
    
    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;
    
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    @TableField("course_name")
    private String courseName;
    
    @NotBlank(message = "课程代码不能为空")
    @Size(max = 20, message = "课程代码长度不能超过20个字符")
    @TableField("course_code")
    private String courseCode;
    
    @TableField("description")
    private String description;
    
    @TableField("credit_hours")
    private Integer creditHours;
    
    @TableField("credits")
    private BigDecimal credits;
    
    @TableField("semester")
    private String semester;
    
    @TableField("academic_year")
    private String academicYear;
    
    @TableField("teacher_id")
    private Long teacherId;
    
    @TableField("dept_id")
    private Long deptId;
    
    @TableField("status")
    private CourseStatus status;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 关联对象
    @TableField(exist = false)
    private Teacher teacher;
    
    @TableField(exist = false)
    private Department department;
    
    /**
     * 课程状态枚举
     */
    public enum CourseStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }
}
