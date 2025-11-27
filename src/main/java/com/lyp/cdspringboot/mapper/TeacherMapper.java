package com.lyp.cdspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyp.cdspringboot.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 根据工号查找教师
     */
    @Select("SELECT * FROM teachers WHERE teacher_number = #{teacherNumber}")
    Teacher findByTeacherNumber(@Param("teacherNumber") String teacherNumber);
    
    /**
     * 根据院系ID查找教师列表
     */
    @Select("SELECT * FROM teachers WHERE dept_id = #{deptId}")
    List<Teacher> findByDeptId(@Param("deptId") Long deptId);
}
