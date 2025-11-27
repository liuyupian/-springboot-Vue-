import request from './request'

// 获取当前学生的提交列表
export function getSubmissionList(params) {
  return request({
    url: '/submissions/list',
    method: 'get',
    params
  })
}

// 提交报告
export const submitReport = (data) => {
  return request({
    url: '/submissions/submit',
    method: 'post',
    data
  })
}

// 评审提交
export const reviewSubmission = (data) => {
  return request({
    url: '/submissions/review',
    method: 'post',
    data
  })
}
