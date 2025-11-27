package com.lyp.cdspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyp.cdspringboot.entity.CourseDesignTask;
import com.lyp.cdspringboot.mapper.CourseDesignTaskMapper;
import com.lyp.cdspringboot.service.ICourseDesignTaskService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CourseDesignTaskServiceImpl extends ServiceImpl<CourseDesignTaskMapper, CourseDesignTask> implements ICourseDesignTaskService {

    @Override
    public IPage<CourseDesignTask> getTaskPage(Long current, Long size, Long courseId, String status, Long createdBy) {
        Page<CourseDesignTask> page = new Page<>(current, size);
        LambdaQueryWrapper<CourseDesignTask> wrapper = new LambdaQueryWrapper<>();
        
        if (courseId != null) {
            wrapper.eq(CourseDesignTask::getCourseId, courseId);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(CourseDesignTask::getStatus, CourseDesignTask.TaskStatus.valueOf(status));
        }
        
        if (createdBy != null) {
            wrapper.eq(CourseDesignTask::getCreatedBy, createdBy);
        }
        
        wrapper.orderByDesc(CourseDesignTask::getCreatedAt);
        
        return this.page(page, wrapper);
    }

    @Override
    public boolean publishTask(CourseDesignTask task) {
        if (task.getStatus() == null) {
            task.setStatus(CourseDesignTask.TaskStatus.DRAFT);
        }
        return this.saveOrUpdate(task);
    }
}
