package com.lyp.cdspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyp.cdspringboot.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    
    /**
     * 根据学号查找学生
     */
    @Select("SELECT * FROM students WHERE student_number = #{studentNumber}")
    Student findByStudentNumber(@Param("studentNumber") String studentNumber);
    
    /**
     * 根据班级ID查找学生列表
     */
    @Select("SELECT * FROM students WHERE class_id = #{classId}")
    List<Student> findByClassId(@Param("classId") Long classId);
    
    /**
     * 根据专业ID查找学生列表
     */
    @Select("SELECT * FROM students WHERE major_id = #{majorId}")
    List<Student> findByMajorId(@Param("majorId") Long majorId);
    
    /**
     * 分页查询学生信息（包含关联信息）
     * TODO: 需要创建XML映射文件实现复杂查询
     */
    // IPage<Student> selectStudentPage(Page<Student> page, @Param("classId") Long classId, 
    //                                @Param("majorId") Long majorId, @Param("grade") Integer grade);
}
