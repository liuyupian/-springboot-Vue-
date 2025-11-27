package com.lyp.cdspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyp.cdspringboot.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
