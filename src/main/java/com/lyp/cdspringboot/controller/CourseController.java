package com.lyp.cdspringboot.controller;

import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.Course;
import com.lyp.cdspringboot.entity.User;
import com.lyp.cdspringboot.service.ICourseService;
import com.lyp.cdspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private ICourseService courseService;
    
    @Autowired
    private UserService userService;


    // 获取课程列表（临时：返回所有课程）
    @GetMapping("/my-courses")
    public Result<List<Course>> getMyCourses() {
        try {
            // 暂时不按教师过滤，直接返回所有课程
            return Result.success(courseService.list());
        } catch (Exception e) {
            return Result.error("获取课程列表失败: " + e.getMessage());
        }
    }
}
