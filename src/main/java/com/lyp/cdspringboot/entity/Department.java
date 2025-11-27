package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 院系实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("departments")
public class Department {
    
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;
    
    @NotBlank(message = "院系名称不能为空")
    @Size(max = 100, message = "院系名称长度不能超过100个字符")
    @TableField("dept_name")
    private String deptName;
    
    @NotBlank(message = "院系代码不能为空")
    @Size(max = 20, message = "院系代码长度不能超过20个字符")
    @TableField("dept_code")
    private String deptCode;
    
    @TableField("description")
    private String description;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
