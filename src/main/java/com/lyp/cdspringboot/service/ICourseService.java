package com.lyp.cdspringboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyp.cdspringboot.entity.Course;
import java.util.List;

public interface ICourseService extends IService<Course> {
    /**
     * 获取教师的课程列表
     */
    List<Course> getCoursesByTeacherId(Long teacherId);
}
