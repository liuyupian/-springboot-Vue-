package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 学生详细信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("students")
public class Student {
    
    @TableId(value = "student_id", type = IdType.INPUT)
    private Long studentId;
    
    @NotBlank(message = "学号不能为空")
    @Size(max = 20, message = "学号长度不能超过20个字符")
    @TableField("student_number")
    private String studentNumber;
    
    @TableField("class_id")
    private Long classId;
    
    @TableField("major_id")
    private Long majorId;
    
    @TableField("grade")
    private Integer grade;
    
    @TableField("enrollment_year")
    private Integer enrollmentYear;
    
    @TableField("advisor_id")
    private Long advisorId;
    
    // 关联对象
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private Class studentClass;
    
    @TableField(exist = false)
    private Major major;
    
    @TableField(exist = false)
    private Teacher advisor;
    
    // 显示用字段
    @TableField(exist = false)
    private String className;
    
    @TableField(exist = false)
    private String majorName;
    
    @TableField(exist = false)
    private String advisorName;
}
