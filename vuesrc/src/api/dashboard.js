import request from './request'

// 获取Dashboard统计数据
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}
