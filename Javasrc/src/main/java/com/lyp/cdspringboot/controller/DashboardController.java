package com.lyp.cdspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lyp.cdspringboot.common.Result;
import com.lyp.cdspringboot.entity.*;
import com.lyp.cdspringboot.mapper.*;
import com.lyp.cdspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseDesignTaskMapper taskMapper;

    @Autowired
    private ReportSubmissionMapper submissionMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ReportReviewMapper reportReviewMapper;

    /**
     * 获取Dashboard统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
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

        Map<String, Object> dashboardData = new HashMap<>();

        if (User.UserType.STUDENT.equals(currentUser.getUserType())) {
            // 学生Dashboard数据
            dashboardData = getStudentDashboardData(currentUser.getUserId());
        } else if (User.UserType.TEACHER.equals(currentUser.getUserType())) {
            // 教师Dashboard数据
            dashboardData = getTeacherDashboardData(currentUser.getUserId());
        } else if (User.UserType.ADMIN.equals(currentUser.getUserType())) {
            // 管理员Dashboard数据
            dashboardData = getAdminDashboardData();
        }

        return Result.success(dashboardData);
    }

    /**
     * 获取学生Dashboard数据
     */
    private Map<String, Object> getStudentDashboardData(Long userId) {
        Map<String, Object> data = new HashMap<>();
        
        Student student = studentMapper.selectById(userId);
        if (student == null) {
            return data;
        }

        // 统计数据
        Map<String, Object> stats = new HashMap<>();
        
        // 总任务数（指导教师发布的任务）
        long totalTasks = 0;
        if (student.getAdvisorId() != null) {
            totalTasks = taskMapper.selectCount(
                new LambdaQueryWrapper<CourseDesignTask>()
                    .eq(CourseDesignTask::getCreatedBy, student.getAdvisorId())
                    .eq(CourseDesignTask::getStatus, CourseDesignTask.TaskStatus.PUBLISHED)
            );
        }
        stats.put("totalTasks", totalTasks);

        // 已提交数
        long submittedCount = submissionMapper.selectCount(
            new LambdaQueryWrapper<ReportSubmission>()
                .eq(ReportSubmission::getStudentId, student.getStudentId())
        );
        stats.put("submittedCount", submittedCount);

        // 已完成数（有评审记录的）
        long completedCount = submissionMapper.selectCount(
            new LambdaQueryWrapper<ReportSubmission>()
                .eq(ReportSubmission::getStudentId, student.getStudentId())
                .exists("SELECT 1 FROM report_reviews r WHERE r.submission_id = report_submissions.submission_id")
        );
        stats.put("completedCount", completedCount);

        // 待提交数
        stats.put("pendingCount", totalTasks - submittedCount);

        data.put("stats", stats);

        // 最近任务
        List<Map<String, Object>> recentTasks = new ArrayList<>();
        if (student.getAdvisorId() != null) {
            List<CourseDesignTask> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<CourseDesignTask>()
                    .eq(CourseDesignTask::getCreatedBy, student.getAdvisorId())
                    .eq(CourseDesignTask::getStatus, CourseDesignTask.TaskStatus.PUBLISHED)
                    .orderByDesc(CourseDesignTask::getCreatedAt)
                    .last("LIMIT 5")
            );

            for (CourseDesignTask task : tasks) {
                Map<String, Object> taskInfo = new HashMap<>();
                taskInfo.put("taskTitle", task.getTaskTitle());
                taskInfo.put("deadline", task.getDueDate());
                taskInfo.put("status", getTaskStatusForStudent(task.getTaskId(), student.getStudentId()));
                recentTasks.add(taskInfo);
            }
        }
        data.put("recentTasks", recentTasks);

        // 最近提交
        List<Map<String, Object>> recentSubmissions = new ArrayList<>();
        List<ReportSubmission> submissions = submissionMapper.selectList(
            new LambdaQueryWrapper<ReportSubmission>()
                .eq(ReportSubmission::getStudentId, student.getStudentId())
                .orderByDesc(ReportSubmission::getSubmittedAt)
                .last("LIMIT 5")
        );

        for (ReportSubmission submission : submissions) {
            Map<String, Object> submissionInfo = new HashMap<>();
            submissionInfo.put("title", submission.getTitle());
            submissionInfo.put("submittedAt", submission.getSubmittedAt());
            submissionInfo.put("status", getSubmissionStatusText(submission.getSubmissionId()));
            recentSubmissions.add(submissionInfo);
        }
        data.put("recentSubmissions", recentSubmissions);

        return data;
    }

    /**
     * 获取教师Dashboard数据
     */
    private Map<String, Object> getTeacherDashboardData(Long userId) {
        Map<String, Object> data = new HashMap<>();
        
        Teacher teacher = teacherMapper.selectById(userId);
        if (teacher == null) {
            return data;
        }

        // 统计数据
        Map<String, Object> stats = new HashMap<>();
        
        // 发布的任务数
        long publishedTasks = taskMapper.selectCount(
            new LambdaQueryWrapper<CourseDesignTask>()
                .eq(CourseDesignTask::getCreatedBy, teacher.getTeacherId())
        );
        stats.put("publishedTasks", publishedTasks);

        // 指导学生数
        long studentCount = studentMapper.selectCount(
            new LambdaQueryWrapper<Student>()
                .eq(Student::getAdvisorId, teacher.getTeacherId())
        );
        stats.put("studentCount", studentCount);

        // 待评审提交数
        long pendingReviews = submissionMapper.selectCount(
            new LambdaQueryWrapper<ReportSubmission>()
                .eq(ReportSubmission::getStatus, ReportSubmission.SubmissionStatus.SUBMITTED)
                .exists("SELECT 1 FROM students s WHERE s.student_id = report_submissions.student_id AND s.advisor_id = " + teacher.getTeacherId())
        );
        stats.put("pendingReviews", pendingReviews);

        // 已评审数
        long completedReviews = reportReviewMapper.selectCount(
            new LambdaQueryWrapper<ReportReview>()
                .eq(ReportReview::getReviewerId, teacher.getTeacherId())
        );
        stats.put("completedReviews", completedReviews);

        data.put("stats", stats);

        // 最近发布的任务
        List<Map<String, Object>> recentTasks = new ArrayList<>();
        List<CourseDesignTask> tasks = taskMapper.selectList(
            new LambdaQueryWrapper<CourseDesignTask>()
                .eq(CourseDesignTask::getCreatedBy, teacher.getTeacherId())
                .orderByDesc(CourseDesignTask::getCreatedAt)
                .last("LIMIT 5")
        );

        for (CourseDesignTask task : tasks) {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("taskTitle", task.getTaskTitle());
            taskInfo.put("deadline", task.getDueDate());
            taskInfo.put("status", task.getStatus().toString());
            recentTasks.add(taskInfo);
        }
        data.put("recentTasks", recentTasks);

        // 最近的提交
        List<Map<String, Object>> recentSubmissions = new ArrayList<>();
        List<ReportSubmission> submissions = submissionMapper.selectList(
            new LambdaQueryWrapper<ReportSubmission>()
                .exists("SELECT 1 FROM students s WHERE s.student_id = report_submissions.student_id AND s.advisor_id = " + teacher.getTeacherId())
                .orderByDesc(ReportSubmission::getSubmittedAt)
                .last("LIMIT 5")
        );

        for (ReportSubmission submission : submissions) {
            Map<String, Object> submissionInfo = new HashMap<>();
            submissionInfo.put("title", submission.getTitle());
            submissionInfo.put("submittedAt", submission.getSubmittedAt());
            submissionInfo.put("status", getSubmissionStatusText(submission.getSubmissionId()));
            recentSubmissions.add(submissionInfo);
        }
        data.put("recentSubmissions", recentSubmissions);

        return data;
    }

    /**
     * 获取管理员Dashboard数据
     */
    private Map<String, Object> getAdminDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // 统计数据
        Map<String, Object> stats = new HashMap<>();
        
        // 总用户数
        long totalUsers = userService.count();
        stats.put("totalUsers", totalUsers);

        // 总任务数
        long totalTasks = taskMapper.selectCount(null);
        stats.put("totalTasks", totalTasks);

        // 总提交数
        long totalSubmissions = submissionMapper.selectCount(null);
        stats.put("totalSubmissions", totalSubmissions);

        // 总评审数
        long totalReviews = reportReviewMapper.selectCount(null);
        stats.put("totalReviews", totalReviews);

        data.put("stats", stats);

        // 最近活动（可以是最近的任务、提交等）
        List<Map<String, Object>> recentActivities = new ArrayList<>();
        // 这里可以添加最近的系统活动
        data.put("recentActivities", recentActivities);

        return data;
    }

    /**
     * 获取学生任务状态
     */
    private String getTaskStatusForStudent(Long taskId, Long studentId) {
        ReportSubmission submission = submissionMapper.selectOne(
            new LambdaQueryWrapper<ReportSubmission>()
                .eq(ReportSubmission::getTaskId, taskId)
                .eq(ReportSubmission::getStudentId, studentId)
        );

        if (submission == null) {
            return "未提交";
        }

        return getSubmissionStatusText(submission.getSubmissionId());
    }

    /**
     * 获取提交状态文本
     */
    private String getSubmissionStatusText(Long submissionId) {
        ReportReview review = reportReviewMapper.selectOne(
            new LambdaQueryWrapper<ReportReview>()
                .eq(ReportReview::getSubmissionId, submissionId)
        );

        if (review == null) {
            return "已提交";
        }

        if ("COMPLETED".equals(review.getReviewStatus())) {
            return review.getScore() != null && review.getScore() >= 60 ? "已通过" : "未通过";
        }

        return "评审中";
    }
}
