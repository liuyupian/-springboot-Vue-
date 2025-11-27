package com.lyp.cdspringboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.CourseDesignTask;
import com.lyp.cdspringboot.entity.User;
import com.lyp.cdspringboot.service.ICourseDesignTaskService;
import com.lyp.cdspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class CourseDesignTaskController {

    @Autowired
    private ICourseDesignTaskService taskService;

    @Autowired
    private UserService userService;

    // 获取任务列表
    @GetMapping("/list")
    public Result<IPage<CourseDesignTask>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status) {

        // 从安全上下文中获取当前登录用户
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

        Long userId = currentUser.getUserId();

        return Result.success(taskService.getTaskPage(current, size, courseId, status, userId));
    }

    // 教师发布/更新任务
    @PostMapping("/save")
    public Result<String> save(@RequestBody CourseDesignTask task) {
        // 从安全上下文中获取当前登录用户
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

        Long userId = currentUser.getUserId();

        // 如果是更新已有任务，只允许编辑草稿任务
        if (task.getTaskId() != null) {
            CourseDesignTask existing = taskService.getById(task.getTaskId());
            if (existing == null) {
                return Result.error("任务不存在");
            }
            if (existing.getStatus() != null && existing.getStatus() != CourseDesignTask.TaskStatus.DRAFT) {
                return Result.error("仅允许编辑草稿任务");
            }
        }

        task.setCreatedBy(userId);

        // 临时修正：如果前端没传 courseId，给一个默认值 1L
        // 后续前端应该增加课程选择下拉框
        if (task.getCourseId() == null) {
            task.setCourseId(1L);
        }
        
        if (taskService.publishTask(task)) {
            return Result.success("保存成功");
        }
        return Result.error("保存失败");
    }

    // 发布任务（将状态置为已发布）
    @PostMapping("/{taskId}/publish")
    public Result<String> publish(@PathVariable Long taskId) {
        CourseDesignTask task = taskService.getById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        task.setStatus(CourseDesignTask.TaskStatus.PUBLISHED);
        boolean success = taskService.saveOrUpdate(task);
        if (success) {
            return Result.success("任务已发布");
        }
        return Result.error("发布失败");
    }
    
    // 删除任务
    @DeleteMapping("/{taskId}")
    public Result<String> delete(@PathVariable Long taskId) {
         if (taskService.removeById(taskId)) {
             return Result.success("删除成功");
         }
         return Result.error("删除失败");
    }
    
    // 获取任务详情
    @GetMapping("/{taskId}")
    public Result<CourseDesignTask> getInfo(@PathVariable Long taskId) {
        return Result.success(taskService.getById(taskId));
    }
}
