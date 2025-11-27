package com.lyp.cdspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyp.cdspringboot.entity.CourseDesignTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程设计任务Mapper接口
 */
@Mapper
public interface CourseDesignTaskMapper extends BaseMapper<CourseDesignTask> {
    
    /**
     * 根据课程ID查找任务列表
     */
    List<CourseDesignTask> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据创建者ID查找任务列表
     */
    List<CourseDesignTask> findByCreatedBy(@Param("createdBy") Long createdBy);
    
    /**
     * 分页查询任务信息（包含关联信息）
     */
    IPage<CourseDesignTask> selectTaskPage(Page<CourseDesignTask> page, 
                                         @Param("courseId") Long courseId,
                                         @Param("status") String status,
                                         @Param("createdBy") Long createdBy);
}
