import request from './request'

// 获取当前用户的课程列表
export function getMyCourses() {
  return request({
    url: '/courses/my-courses',
    method: 'get'
  })
}
