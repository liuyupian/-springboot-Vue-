package com.lyp.cdspringboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyp.cdspringboot.entity.CourseDesignTask;

public interface ICourseDesignTaskService extends IService<CourseDesignTask> {
    
    /**
     * 分页查询任务列表
     * @param current 当前页
     * @param size 每页大小
     * @param courseId 课程ID (可选)
     * @param status 任务状态 (可选)
     * @param createdBy 创建者ID (可选)
     * @return 分页结果
     */
    IPage<CourseDesignTask> getTaskPage(Long current, Long size, Long courseId, String status, Long createdBy);
    
    /**
     * 发布/保存任务
     * @param task 任务信息
     * @return 是否成功
     */
    boolean publishTask(CourseDesignTask task);
}
