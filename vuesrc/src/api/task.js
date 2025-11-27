import request from './request'

// 获取任务列表
export function getTaskList(params) {
  return request({
    url: '/tasks/list',
    method: 'get',
    params
  })
}

// 创建任务
export function createTask(data) {
  return request({
    url: '/tasks/save',
    method: 'post',
    data
  })
}

// 删除任务
export function deleteTask(taskId) {
  return request({
    url: `/tasks/${taskId}`,
    method: 'delete'
  })
}

// 发布任务
export function publishTask(taskId) {
  return request({
    url: `/tasks/${taskId}/publish`,
    method: 'post'
  })
}

// 学生端：获取可见任务列表（按指导教师、已发布）
export function getStudentTaskList(params) {
  return request({
    url: '/student/tasks/list',
    method: 'get',
    params
  })
}
