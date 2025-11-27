<template>
  <Layout>
    <div class="submissions">
      <div class="page-header">
        <h2>{{ userStore.isStudent ? '我的提交' : '提交管理' }}</h2>
      </div>

      <!-- 学生端：可提交任务列表 -->
      <el-card v-if="userStore.isStudent" class="tasks-card" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>可提交任务</span>
          </div>
        </template>
        <el-table :data="availableTasks" style="width: 100%" v-loading="tasksLoading">
          <el-table-column prop="taskTitle" label="任务标题" />
          <el-table-column prop="courseName" label="课程" width="200" />
          <el-table-column prop="dueDate" label="截止时间" width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getTaskStatusType(scope.row.status)">
                {{ getTaskStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button class="action-btn submit-btn" size="small" @click="goSubmit(scope.row)">去提交</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 搜索和筛选 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="提交标题">
            <el-input v-model="searchForm.title" placeholder="请输入提交标题" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已提交" value="SUBMITTED" />
              <el-option label="评审中" value="UNDER_REVIEW" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="search-btn" @click="handleSearch">搜索</el-button>
            <el-button class="reset-btn" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 提交列表 -->
      <el-card>
        <el-table :data="submissions" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="提交标题" />
          <el-table-column prop="taskTitle" label="任务" width="200" />
          <el-table-column prop="courseName" label="课程" width="200" />
          <el-table-column prop="studentName" label="学生" width="120" v-if="!userStore.isStudent" />
          <el-table-column prop="submittedAt" label="提交时间" width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="评分" width="80" v-if="!userStore.isStudent">
            <template #default="scope">
              {{ scope.row.score || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240">
            <template #default="scope">
              <el-button class="action-btn view-btn" size="small" @click="viewSubmission(scope.row)">查看</el-button>
              <el-button 
                class="action-btn edit-btn" 
                size="small" 
                @click="editSubmission(scope.row)"
                v-if="userStore.isStudent && scope.row.status === 'DRAFT'"
              >
                编辑
              </el-button>
              <el-button 
                class="action-btn review-btn" 
                size="small" 
                @click="reviewSubmission(scope.row)"
                v-if="!userStore.isStudent && scope.row.status === 'SUBMITTED'"
              >
                评审
              </el-button>
              <el-button 
                class="action-btn download-btn" 
                size="small" 
                @click="downloadFiles(scope.row)"
              >
                下载
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          style="margin-top: 20px; text-align: right;"
        />
      </el-card>

      <!-- 提交详情对话框 -->
      <el-dialog
        v-model="showDetailDialog"
        title="提交详情"
        width="800px"
      >
        <div v-if="currentSubmission">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="提交标题">{{ currentSubmission.title }}</el-descriptions-item>
            <el-descriptions-item label="任务">{{ currentSubmission.taskTitle }}</el-descriptions-item>
            <el-descriptions-item label="学生">{{ currentSubmission.studentName }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ currentSubmission.submittedAt }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentSubmission.status)">
                {{ getStatusText(currentSubmission.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="评分">{{ currentSubmission.score || '未评分' }}</el-descriptions-item>
          </el-descriptions>
          
          <div style="margin-top: 20px;">
            <h4>提交描述</h4>
            <p>{{ currentSubmission.description || '无描述' }}</p>
          </div>
          
          <div style="margin-top: 20px;">
            <h4>提交文件</h4>
            <el-table :data="currentSubmission.fileInfos || []" style="width: 100%">
              <el-table-column prop="fileName" label="文件名" />
              <el-table-column prop="fileSize" label="文件大小" width="120">
                <template #default="scope">
                  {{ formatFileSize(scope.row.fileSize) }}
                </template>
              </el-table-column>
              <el-table-column prop="fileType" label="文件类型" width="100" />
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button type="text" size="small" @click="downloadFile(scope.row)">下载</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="!currentSubmission.fileInfos || currentSubmission.fileInfos.length === 0" style="text-align: center; color: #999; padding: 20px;">
              暂无文件
            </div>
          </div>
          
          <div style="margin-top: 20px;" v-if="currentSubmission.feedback">
            <h4>评审反馈</h4>
            <p>{{ currentSubmission.feedback }}</p>
          </div>
        </div>
      </el-dialog>

      <!-- 学生端：提交报告对话框 -->
      <el-dialog
        v-model="showSubmitDialog"
        title="提交课程设计报告"
        width="600px"
      >
        <el-form ref="submitFormRef" :model="submitForm" :rules="submitRules" label-width="100px">
          <el-form-item label="提交标题" prop="title">
            <el-input v-model="submitForm.title" placeholder="请输入提交标题" />
          </el-form-item>
          <el-form-item label="提交说明" prop="description">
            <el-input
              v-model="submitForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入对本次提交的说明"
            />
          </el-form-item>
          <el-form-item label="上传文件">
            <el-upload
              action=""
              :auto-upload="true"
              :file-list="fileList"
              :http-request="handleUpload"
              :on-remove="handleRemove"
              multiple
            >
              <el-button type="primary">选择文件</el-button>
              <div class="el-upload__tip">支持上传多个文件，大小和类型限制可后续配置</div>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showSubmitDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitReport">提交</el-button>
        </template>
      </el-dialog>

      <!-- 评审对话框 -->
      <el-dialog
        v-model="showReviewDialog"
        title="评审提交"
        width="600px"
      >
        <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="80px">
          <el-form-item label="评分" prop="score">
            <el-input-number 
              v-model="reviewForm.score" 
              :min="0" 
              :max="100" 
              placeholder="请输入评分"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="评语" prop="feedback">
            <el-input
              v-model="reviewForm.feedback"
              type="textarea"
              :rows="6"
              placeholder="请输入评语"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="reviewForm.status">
              <el-radio label="PASS">通过</el-radio>
              <el-radio label="FAIL">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showReviewDialog = false">取消</el-button>
          <el-button type="primary" @click="handleReview" :loading="reviewing">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getStudentTaskList } from '@/api/task'
import { getSubmissionList, submitReport, reviewSubmission as reviewSubmissionAPI } from '@/api/submission'
import { uploadFile } from '@/api/file'
import Layout from '@/components/Layout.vue'

const userStore = useUserStore()

const loading = ref(false)
const tasksLoading = ref(false)
const reviewing = ref(false)
const showDetailDialog = ref(false)
const showReviewDialog = ref(false)
const currentSubmission = ref(null)
const reviewFormRef = ref()
const submitFormRef = ref()
const fileList = ref([])        // el-upload 展示用
const uploadedFiles = ref([])   // 已上传成功的文件信息

const searchForm = reactive({
  title: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const reviewForm = reactive({
  score: null,
  feedback: '',
  status: 'PASS'
})

const reviewRules = {
  score: [
    { required: true, message: '请输入评分', trigger: 'blur' }
  ],
  feedback: [
    { required: true, message: '请输入评语', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 学生端：可提交任务列表
const availableTasks = ref([])

const getTaskStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'CLOSED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getTaskStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'CLOSED': '已关闭'
  }
  return textMap[status] || status
}

const loadAvailableTasks = async () => {
  if (!userStore.isStudent) return
  tasksLoading.value = true
  try {
    const res = await getStudentTaskList({ current: 1, size: 100 })
    if (res.code === 200) {
      availableTasks.value = res.data.records || []
    }
  } catch (error) {
    ElMessage.error('获取可提交任务失败')
  } finally {
    tasksLoading.value = false
  }
}

// 提交列表数据
const submissions = ref([])

// 学生提交表单
const showSubmitDialog = ref(false)
const submitForm = reactive({
  taskId: null,
  title: '',
  description: '',
  files: []
})

const submitRules = {
  title: [
    { required: true, message: '请输入提交标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入提交说明', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'SUBMITTED': 'warning',
    'UNDER_REVIEW': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'SUBMITTED': '已提交',
    'UNDER_REVIEW': '评审中',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return textMap[status] || status
}

const formatFileSize = (size) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(1) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

const handleSearch = () => {
  pagination.page = 1
  loadSubmissions()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = ''
  pagination.page = 1
  loadSubmissions()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadSubmissions()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadSubmissions()
}

const loadSubmissions = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
      // 后续可以加按任务、状态过滤
    }
    const res = await getSubmissionList(params)
    if (res.code === 200) {
      submissions.value = res.data.records || []
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取提交列表失败')
  } finally {
    loading.value = false
  }
}

const viewSubmission = (submission) => {
  currentSubmission.value = submission
  showDetailDialog.value = true
}

const editSubmission = (submission) => {
  // 跳转到编辑页面
  console.log('编辑提交:', submission)
}

const reviewSubmission = (submission) => {
  currentSubmission.value = submission
  reviewForm.score = submission.score
  reviewForm.feedback = submission.feedback || ''
  reviewForm.status = 'PASS'
  showReviewDialog.value = true
}

const handleReview = async () => {
  if (!reviewFormRef.value) return
  
  await reviewFormRef.value.validate(async (valid) => {
    if (valid) {
      reviewing.value = true
      try {
        const reviewData = {
          submissionId: currentSubmission.value.submissionId,
          score: reviewForm.score,
          feedback: reviewForm.feedback,
          status: reviewForm.status
        }
        const res = await reviewSubmissionAPI(reviewData)
        if (res.code === 200) {
          ElMessage.success('评审成功')
          showReviewDialog.value = false
          loadSubmissions()
        } else {
          ElMessage.error(res.message || '评审失败')
        }
      } catch (error) {
        ElMessage.error('评审失败')
      } finally {
        reviewing.value = false
      }
    }
  })
}

// 文件上传：使用自定义 http-request
const handleUpload = async (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await uploadFile(formData)
    if (res.code === 200) {
      const info = res.data
      uploadedFiles.value.push({
        fileId: info.fileId,
        fileName: info.fileName,
        fileSize: info.fileSize,
        fileType: info.fileType
      })
      fileList.value.push(file)
    } else if (res.message) {
      ElMessage.error(res.message)
    }
  } catch (e) {
    ElMessage.error('文件上传失败')
  }
}

const handleRemove = (file) => {
  fileList.value = fileList.value.filter(f => f.uid !== file.uid)
  uploadedFiles.value = uploadedFiles.value.filter(f => f.fileName !== file.name)
}


const downloadFiles = (submission) => {
  // 下载提交的所有文件
  if (submission.fileInfos && submission.fileInfos.length > 0) {
    submission.fileInfos.forEach(file => {
      downloadFile(file)
    })
  } else {
    ElMessage.warning('该提交暂无文件')
  }
}

const downloadFile = (file) => {
  // 下载单个文件
  if (file.fileId) {
    const downloadUrl = `http://localhost:8080/api/files/download/${file.fileId}`
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = file.fileName || 'download'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } else {
    ElMessage.error('文件ID不存在')
  }
}

// 学生端：打开提交对话框
const goSubmit = (task) => {
  submitForm.taskId = task.taskId
  submitForm.title = task.taskTitle
  submitForm.description = ''
  submitForm.files = []
  fileList.value = []
  uploadedFiles.value = []
  showSubmitDialog.value = true
}

// 学生端：提交报告
const handleSubmitReport = async () => {
  if (!submitFormRef.value) return

  await submitFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitForm.files = uploadedFiles.value.map(f => f.fileId)
        const res = await submitReport(submitForm)
        if (res.code === 200) {
          ElMessage.success('提交成功')
          showSubmitDialog.value = false
          loadSubmissions()
        } else if (res.message) {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error('提交失败')
      }
    }
  })
}

onMounted(() => {
  loadAvailableTasks()
  loadSubmissions()
})
</script>

<style scoped>
.submissions {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

/* 现代化按钮样式 */
.search-btn {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
}

.reset-btn {
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px 16px;
  color: #666;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background: #e9e9e9;
  border-color: #ccc;
  transform: translateY(-1px);
}

/* 表格操作按钮 */
.action-btn {
  border: none;
  border-radius: 4px;
  padding: 4px 12px;
  margin: 0 2px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.view-btn {
  background: linear-gradient(135deg, #a8edea, #fed6e3);
  color: #333;
}

.view-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(168, 237, 234, 0.4);
}

.edit-btn {
  background: linear-gradient(135deg, #ffecd2, #fcb69f);
  color: #333;
}

.edit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 236, 210, 0.4);
}

.review-btn {
  background: linear-gradient(135deg, #d299c2, #fef9d7);
  color: #333;
}

.review-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(210, 153, 194, 0.4);
}

.download-btn {
  background: linear-gradient(135deg, #89f7fe, #66a6ff);
  color: #333;
}

.download-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(137, 247, 254, 0.4);
}

.submit-btn {
  background: linear-gradient(135deg, #96fbc4, #f9f047);
  color: #333;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(150, 251, 196, 0.4);
}
</style>
