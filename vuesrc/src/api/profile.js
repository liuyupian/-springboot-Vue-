import request from './request'

// 获取用户详细信息
export function getUserProfile() {
  return request({
    url: '/profile/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateProfile(data) {
  return request({
    url: '/profile/update',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/profile/password',
    method: 'put',
    data
  })
}
