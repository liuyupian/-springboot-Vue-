package com.lyp.cdspringboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.CourseDesignTask;
import com.lyp.cdspringboot.entity.Student;
import com.lyp.cdspringboot.entity.User;
import com.lyp.cdspringboot.service.ICourseDesignTaskService;
import com.lyp.cdspringboot.service.UserService;
import com.lyp.cdspringboot.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生端查看任务相关接口
 */
@RestController
@RequestMapping("/api/student/tasks")
public class StudentTaskController {

    @Autowired
    private ICourseDesignTaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 学生查看自己可见的任务列表：
     * 按当前学生的指导教师（advisor_id）过滤，且只返回已发布任务
     */
    @GetMapping("/list")
    public Result<IPage<CourseDesignTask>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("未登录或登录已失效");
        }

        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        // 假设 students.student_id 与 users.user_id 对应
        Student student = studentMapper.selectById(currentUser.getUserId());
        if (student == null) {
            return Result.error("学生信息不存在");
        }

        Long advisorId = student.getAdvisorId();
        if (advisorId == null) {
            return Result.error("未设置指导教师，无法获取任务");
        }

        // 只返回该指导教师创建、状态为 PUBLISHED 的任务
        IPage<CourseDesignTask> page = taskService.getTaskPage(
                current,
                size,
                null,
                CourseDesignTask.TaskStatus.PUBLISHED.name(),
                advisorId
        );

        return Result.success(page);
    }
}
