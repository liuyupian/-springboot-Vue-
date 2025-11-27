package com.lyp.cdspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyp.cdspringboot.entity.Course;
import com.lyp.cdspringboot.entity.CourseDesignTask;
import com.lyp.cdspringboot.entity.FileStorage;
import com.lyp.cdspringboot.entity.ReportReview;
import com.lyp.cdspringboot.entity.ReportSubmission;
import com.lyp.cdspringboot.entity.Student;
import com.lyp.cdspringboot.entity.SubmissionFile;
import com.lyp.cdspringboot.entity.User;
import com.lyp.cdspringboot.mapper.CourseDesignTaskMapper;
import com.lyp.cdspringboot.mapper.CourseMapper;
import com.lyp.cdspringboot.mapper.FileStorageMapper;
import com.lyp.cdspringboot.mapper.ReportReviewMapper;
import com.lyp.cdspringboot.mapper.ReportSubmissionMapper;
import com.lyp.cdspringboot.mapper.StudentMapper;
import com.lyp.cdspringboot.mapper.SubmissionFileMapper;
import com.lyp.cdspringboot.mapper.UserMapper;
import com.lyp.cdspringboot.service.IReportSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportSubmissionServiceImpl extends ServiceImpl<ReportSubmissionMapper, ReportSubmission> implements IReportSubmissionService {

    @Autowired
    private CourseDesignTaskMapper taskMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SubmissionFileMapper submissionFileMapper;

    @Autowired
    private FileStorageMapper fileStorageMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReportReviewMapper reportReviewMapper;
    
    @Override
    public IPage<ReportSubmission> getSubmissionPage(Long current, Long size, Long taskId, Long studentId) {
        Page<ReportSubmission> page = new Page<>(current, size);
        LambdaQueryWrapper<ReportSubmission> wrapper = new LambdaQueryWrapper<>();
        
        if (taskId != null) {
            wrapper.eq(ReportSubmission::getTaskId, taskId);
        }
        if (studentId != null) {
            wrapper.eq(ReportSubmission::getStudentId, studentId);
        }
        
        wrapper.orderByDesc(ReportSubmission::getSubmittedAt);
        IPage<ReportSubmission> resultPage = this.page(page, wrapper);

        // 为前端展示补充任务标题和课程名称
        if (resultPage.getRecords() != null) {
            for (ReportSubmission submission : resultPage.getRecords()) {
                if (submission.getTaskId() != null) {
                    CourseDesignTask task = taskMapper.selectById(submission.getTaskId());
                    if (task != null) {
                        submission.setTaskTitle(task.getTaskTitle());
                        if (task.getCourseId() != null) {
                            Course course = courseMapper.selectById(task.getCourseId());
                            if (course != null) {
                                submission.setCourseName(course.getCourseName());
                            }
                        }
                    }
                }

                // 加载学生姓名
                loadStudentName(submission);
                
                // 加载评审信息
                loadReviewInfo(submission);
                
                // 加载该提交关联的文件信息
                loadSubmissionFiles(submission);
            }
        }

        return resultPage;
    }

    @Override
    public IPage<ReportSubmission> getSubmissionPageByAdvisor(Long current, Long size, Long taskId, Long advisorId) {
        Page<ReportSubmission> page = new Page<>(current, size);
        
        System.out.println("[DEBUG] 查询指导教师ID: " + advisorId + " 的学生");
        
        // 先查询该指导教师的所有学生
        List<Student> students = studentMapper.selectList(
            new LambdaQueryWrapper<Student>()
                .eq(Student::getAdvisorId, advisorId)
        );
        
        System.out.println("[DEBUG] 找到学生数量: " + students.size());
        for (Student student : students) {
            System.out.println("[DEBUG] 学生ID: " + student.getStudentId() + ", advisor_id: " + student.getAdvisorId());
        }
        
        if (students.isEmpty()) {
            // 没有指导学生，临时返回所有提交记录用于调试
            System.out.println("[DEBUG] 没有找到指导学生，临时返回所有提交记录");
            LambdaQueryWrapper<ReportSubmission> wrapper = new LambdaQueryWrapper<>();
            if (taskId != null) {
                wrapper.eq(ReportSubmission::getTaskId, taskId);
            }
            wrapper.orderByDesc(ReportSubmission::getSubmittedAt);
            IPage<ReportSubmission> resultPage = this.page(page, wrapper);
            
            // 为前端展示补充信息
            if (resultPage.getRecords() != null) {
                for (ReportSubmission submission : resultPage.getRecords()) {
                    if (submission.getTaskId() != null) {
                        CourseDesignTask task = taskMapper.selectById(submission.getTaskId());
                        if (task != null) {
                            submission.setTaskTitle(task.getTaskTitle());
                            if (task.getCourseId() != null) {
                                Course course = courseMapper.selectById(task.getCourseId());
                                if (course != null) {
                                    submission.setCourseName(course.getCourseName());
                                }
                            }
                        }
                    }
                    loadStudentName(submission);
                    loadReviewInfo(submission);
                    loadSubmissionFiles(submission);
                }
            }
            return resultPage;
        }
        
        // 提取学生ID列表
        List<Long> studentIds = students.stream()
            .map(Student::getStudentId)
            .collect(java.util.stream.Collectors.toList());
        
        System.out.println("[DEBUG] 学生ID列表: " + studentIds);
        
        LambdaQueryWrapper<ReportSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ReportSubmission::getStudentId, studentIds);
        
        if (taskId != null) {
            wrapper.eq(ReportSubmission::getTaskId, taskId);
        }
        
        wrapper.orderByDesc(ReportSubmission::getSubmittedAt);
        
        IPage<ReportSubmission> resultPage = this.page(page, wrapper);
        System.out.println("[DEBUG] 查询到提交记录数量: " + resultPage.getRecords().size());
        
        // 为前端展示补充任务标题、课程名称和学生信息
        if (resultPage.getRecords() != null) {
            for (ReportSubmission submission : resultPage.getRecords()) {
                // 补充任务和课程信息
                if (submission.getTaskId() != null) {
                    CourseDesignTask task = taskMapper.selectById(submission.getTaskId());
                    if (task != null) {
                        submission.setTaskTitle(task.getTaskTitle());
                        if (task.getCourseId() != null) {
                            Course course = courseMapper.selectById(task.getCourseId());
                            if (course != null) {
                                submission.setCourseName(course.getCourseName());
                            }
                        }
                    }
                }
                
                // 加载学生姓名
                loadStudentName(submission);
                
                // 加载评审信息
                loadReviewInfo(submission);
                
                // 加载该提交关联的文件信息
                loadSubmissionFiles(submission);
            }
        }
        
        return resultPage;
    }

    @Override
    public boolean submitReport(ReportSubmission submission) {
        if (submission.getStatus() == null) {
            submission.setStatus(ReportSubmission.SubmissionStatus.SUBMITTED);
        }
        submission.setSubmittedAt(LocalDateTime.now());
        boolean ok = this.save(submission);
        if (!ok) {
            return false;
        }

        // 处理与文件的关联
        if (submission.getFiles() != null) {
            int order = 1;
            for (Long fileId : submission.getFiles()) {
                if (fileId == null) continue;
                SubmissionFile relation = new SubmissionFile();
                relation.setSubmissionId(submission.getSubmissionId());
                relation.setFileId(fileId);
                relation.setFileOrder(order++);
                submissionFileMapper.insert(relation);
            }
        }

        return true;
    }

    @Override
    public boolean reviewSubmission(ReportSubmission reviewData, Long reviewerId) {
        if (reviewData.getSubmissionId() == null) {
            return false;
        }
        
        // 查询原提交记录
        ReportSubmission existing = this.getById(reviewData.getSubmissionId());
        if (existing == null) {
            return false;
        }
        
        // 查询是否已有评审记录
        ReportReview existingReview = reportReviewMapper.selectOne(
            new LambdaQueryWrapper<ReportReview>()
                .eq(ReportReview::getSubmissionId, reviewData.getSubmissionId())
        );
        
        ReportReview review;
        if (existingReview != null) {
            // 更新现有评审
            review = existingReview;
        } else {
            // 创建新评审
            review = new ReportReview();
            review.setSubmissionId(reviewData.getSubmissionId());
            review.setReviewerId(reviewerId);
        }
        
        // 设置评审信息
        review.setScore(reviewData.getScore());
        review.setFeedback(reviewData.getFeedback());
        review.setReviewedAt(LocalDateTime.now());
        
        // 直接使用数据库ENUM值
        if (reviewData.getScore() != null) {
            review.setReviewStatus("COMPLETED");
            // 暂时注释掉更新submission状态，先让评审记录能保存成功
            // if (reviewData.getScore() >= 60) {
            //     existing.setStatus(ReportSubmission.SubmissionStatus.PASS);
            // } else {
            //     existing.setStatus(ReportSubmission.SubmissionStatus.FAIL);
            // }
        } else {
            review.setReviewStatus("PENDING");
            // existing.setStatus(ReportSubmission.SubmissionStatus.UNDER_REVIEW);
        }
        
        // 保存评审记录
        boolean reviewSaved;
        if (existingReview != null) {
            reviewSaved = reportReviewMapper.updateById(review) > 0;
        } else {
            reviewSaved = reportReviewMapper.insert(review) > 0;
        }
        
        // 暂时不更新提交状态，先让评审功能能正常工作
        // boolean submissionUpdated = this.updateById(existing);
        
        return reviewSaved; // && submissionUpdated;
    }

    /**
     * 为提交记录加载关联的文件信息
     */
    private void loadSubmissionFiles(ReportSubmission submission) {
        if (submission.getSubmissionId() == null) return;
        
        // 查询该提交关联的文件
        List<SubmissionFile> submissionFiles = submissionFileMapper.selectList(
            new LambdaQueryWrapper<SubmissionFile>()
                .eq(SubmissionFile::getSubmissionId, submission.getSubmissionId())
                .orderByAsc(SubmissionFile::getFileOrder)
        );
        
        List<Map<String, Object>> files = new ArrayList<>();
        for (SubmissionFile sf : submissionFiles) {
            if (sf.getFileId() != null) {
                FileStorage fileStorage = fileStorageMapper.selectById(sf.getFileId());
                if (fileStorage != null && !fileStorage.getIsDeleted()) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("fileId", fileStorage.getFileId());
                    fileInfo.put("fileName", fileStorage.getFileName());
                    fileInfo.put("fileSize", fileStorage.getFileSize());
                    fileInfo.put("fileType", fileStorage.getFileType());
                    files.add(fileInfo);
                }
            }
        }
        
        // 设置到提交记录的非持久化字段中
        submission.setFileInfos(files);
    }

    /**
     * 为提交记录加载学生姓名
     */
    private void loadStudentName(ReportSubmission submission) {
        if (submission.getStudentId() == null) return;
        
        Student student = studentMapper.selectById(submission.getStudentId());
        if (student != null) {
            User user = userMapper.selectById(student.getStudentId());
            if (user != null) {
                submission.setStudentName(user.getRealName());
            }
        }
    }
    
    /**
     * 为提交记录加载评审信息
     */
    private void loadReviewInfo(ReportSubmission submission) {
        if (submission.getSubmissionId() == null) return;
        
        ReportReview review = reportReviewMapper.selectOne(
            new LambdaQueryWrapper<ReportReview>()
                .eq(ReportReview::getSubmissionId, submission.getSubmissionId())
        );
        
        if (review != null) {
            submission.setScore(review.getScore());
            submission.setFeedback(review.getFeedback());
            submission.setReviewedAt(review.getReviewedAt());
        }
    }
}
