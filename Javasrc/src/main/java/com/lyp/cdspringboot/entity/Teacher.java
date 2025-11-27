package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 教师详细信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teachers")
public class Teacher {
    
    @TableId(value = "teacher_id", type = IdType.INPUT)
    private Long teacherId;
    
    @NotBlank(message = "工号不能为空")
    @Size(max = 20, message = "工号长度不能超过20个字符")
    @TableField("teacher_number")
    private String teacherNumber;
    
    @TableField("dept_id")
    private Long deptId;
    
    @TableField("title")
    private String title;
    
    @TableField("research_area")
    private String researchArea;
    
    @TableField("office_location")
    private String officeLocation;
    
    // 关联对象
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private Department department;
    
    // 显示用字段
    @TableField(exist = false)
    private String deptName;
}
