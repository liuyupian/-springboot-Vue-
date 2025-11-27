package com.lyp.cdspringboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyp.cdspringboot.entity.ReportSubmission;

public interface IReportSubmissionService extends IService<ReportSubmission> {
    
    /**
     * 分页查询提交记录
     * @param current 当前页
     * @param size 每页大小
     * @param taskId 任务ID (可选)
     * @param studentId 学生ID (可选)
     * @return 分页结果
     */
    IPage<ReportSubmission> getSubmissionPage(Long current, Long size, Long taskId, Long studentId);
    
    /**
     * 根据指导教师获取分页提交列表
     */
    IPage<ReportSubmission> getSubmissionPageByAdvisor(Long current, Long size, Long taskId, Long advisorId);
    
    /**
     * 提交报告
     * @param submission 提交信息
     * @return 是否成功
     */
    boolean submitReport(ReportSubmission submission);

    /**
     * 评审提交
     * @param reviewData 评审信息（包含提交ID、评分、反馈等）
     * @param reviewerId 评审者ID
     * @return 是否成功
     */
    boolean reviewSubmission(ReportSubmission reviewData, Long reviewerId);
}
