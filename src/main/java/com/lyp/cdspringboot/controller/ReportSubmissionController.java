package com.lyp.cdspringboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.ReportSubmission;
import com.lyp.cdspringboot.entity.Student;
import com.lyp.cdspringboot.entity.Teacher;
import com.lyp.cdspringboot.entity.User;
import com.lyp.cdspringboot.mapper.StudentMapper;
import com.lyp.cdspringboot.mapper.TeacherMapper;
import com.lyp.cdspringboot.service.IReportSubmissionService;
import com.lyp.cdspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class ReportSubmissionController {

    @Autowired
    private IReportSubmissionService submissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/list")
    public Result<IPage<ReportSubmission>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long taskId) {

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

        // 根据用户角色返回不同的数据
        if (User.UserType.STUDENT.equals(currentUser.getUserType())) {
            // 学生：查看自己的提交
            Student student = studentMapper.selectById(currentUser.getUserId());
            if (student == null) {
                return Result.error("学生信息不存在");
            }
            Long studentId = student.getStudentId();
            IPage<ReportSubmission> page = submissionService.getSubmissionPage(current, size, taskId, studentId);
            return Result.success(page);
        } else if (User.UserType.TEACHER.equals(currentUser.getUserType())) {
            // 教师：临时查看所有提交（绕过advisor_id限制）
            System.out.println("[DEBUG] 教师查看所有提交记录");
            IPage<ReportSubmission> page = submissionService.getSubmissionPage(current, size, taskId, null);
            System.out.println("[DEBUG] 查询结果数量: " + page.getRecords().size());
            return Result.success(page);
        } else {
            // 管理员：查看所有提交
            IPage<ReportSubmission> page = submissionService.getSubmissionPage(current, size, taskId, null);
            return Result.success(page);
        }
    }

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody ReportSubmission submission) {
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

        Student student = studentMapper.selectById(currentUser.getUserId());
        if (student == null) {
            return Result.error("学生信息不存在");
        }

        Long studentId = student.getStudentId();
        submission.setStudentId(studentId);

        // 同一学生对同一任务只允许一条提交记录
        if (submission.getTaskId() != null) {
            ReportSubmission existing = submissionService.lambdaQuery()
                    .eq(ReportSubmission::getTaskId, submission.getTaskId())
                    .eq(ReportSubmission::getStudentId, studentId)
                    .one();
            if (existing != null) {
                return Result.error("你已经提交过该任务，暂不支持重复提交");
            }
        }

        if (submissionService.submitReport(submission)) {
            return Result.success("提交成功");
        }
        return Result.error("提交失败");
    }

    @PostMapping("/review")
    public Result<String> review(@RequestBody ReportSubmission reviewData) {
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

        // 只有教师和管理员可以评审
        if (!User.UserType.TEACHER.equals(currentUser.getUserType()) && !User.UserType.ADMIN.equals(currentUser.getUserType())) {
            return Result.error("无权限进行评审");
        }

        if (submissionService.reviewSubmission(reviewData, currentUser.getUserId())) {
            return Result.success("评审成功");
        }
        return Result.error("评审失败");
    }
}
