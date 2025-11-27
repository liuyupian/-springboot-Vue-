package com.lyp.cdspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyp.cdspringboot.entity.ReportSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报告提交Mapper接口
 */
@Mapper
public interface ReportSubmissionMapper extends BaseMapper<ReportSubmission> {
    
    /**
     * 根据任务ID查找提交列表
     */
    List<ReportSubmission> findByTaskId(@Param("taskId") Long taskId);
    
    /**
     * 根据学生ID查找提交列表
     */
    List<ReportSubmission> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据任务ID和学生ID查找提交记录
     */
    ReportSubmission findByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") Long studentId);
    
    /**
     * 分页查询提交信息（包含关联信息）
     */
    IPage<ReportSubmission> selectSubmissionPage(Page<ReportSubmission> page,
                                               @Param("taskId") Long taskId,
                                               @Param("studentId") Long studentId,
                                               @Param("status") String status);
}
