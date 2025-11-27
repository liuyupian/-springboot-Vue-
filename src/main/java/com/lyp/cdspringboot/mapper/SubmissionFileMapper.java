package com.lyp.cdspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyp.cdspringboot.entity.SubmissionFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提交-文件关系表 Mapper
 */
@Mapper
public interface SubmissionFileMapper extends BaseMapper<SubmissionFile> {
}
