package com.lyp.cdspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyp.cdspringboot.entity.Course;
import com.lyp.cdspringboot.mapper.CourseMapper;
import com.lyp.cdspringboot.service.ICourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Override
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        // 默认查询活跃课程
      //  wrapper.eq(Course::getStatus, Course.CourseStatus.ACTIVE);
        return this.list(wrapper);
    }
}
