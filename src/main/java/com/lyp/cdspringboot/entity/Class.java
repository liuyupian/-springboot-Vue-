package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 班级信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("classes")
public class Class {
    
    @TableId(value = "class_id", type = IdType.AUTO)
    private Long classId;
    
    @NotBlank(message = "班级名称不能为空")
    @Size(max = 100, message = "班级名称长度不能超过100个字符")
    @TableField("class_name")
    private String className;
    
    @NotBlank(message = "班级代码不能为空")
    @Size(max = 20, message = "班级代码长度不能超过20个字符")
    @TableField("class_code")
    private String classCode;
    
    @TableField("major_id")
    private Long majorId;
    
    @TableField("grade")
    private Integer grade;
    
    @TableField("teacher_id")
    private Long teacherId;
    
    @TableField("student_count")
    private Integer studentCount;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 关联对象
    @TableField(exist = false)
    private Major major;
    
    @TableField(exist = false)
    private Teacher teacher;
}
