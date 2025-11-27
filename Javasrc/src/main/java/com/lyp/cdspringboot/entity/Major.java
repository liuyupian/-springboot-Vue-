package com.lyp.cdspringboot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 专业实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("majors")
public class Major {
    
    @TableId(value = "major_id", type = IdType.AUTO)
    private Long majorId;
    
    @NotBlank(message = "专业名称不能为空")
    @Size(max = 100, message = "专业名称长度不能超过100个字符")
    @TableField("major_name")
    private String majorName;
    
    @NotBlank(message = "专业代码不能为空")
    @Size(max = 20, message = "专业代码长度不能超过20个字符")
    @TableField("major_code")
    private String majorCode;
    
    @TableField("dept_id")
    private Long deptId;
    
    @TableField("degree_type")
    private DegreeType degreeType;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 关联对象
    @TableField(exist = false)
    private Department department;
    
    /**
     * 学位类型枚举
     */
    public enum DegreeType {
        BACHELOR, MASTER, DOCTOR
    }
}
