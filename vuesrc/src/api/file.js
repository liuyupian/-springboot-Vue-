import request from './request'

// 文件上传
export function uploadFile(data) {
  return request({
    url: '/files/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
