<template>
  <Layout>
    <div class="tasks">
      <div class="page-header">
        <h2>任务管理</h2>
        <el-button class="create-btn" @click="handleCreate" v-if="userStore.isTeacher || userStore.isAdmin">
          <el-icon><Plus /></el-icon>
          创建任务
        </el-button>
      </div>

      <!-- 搜索和筛选 -->
      <el-card class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="任务标题">
            <el-input v-model="searchForm.title" placeholder="请输入任务标题" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="已关闭" value="CLOSED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="search-btn" @click="handleSearch">搜索</el-button>
            <el-button class="reset-btn" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 任务详情对话框 -->
      <el-dialog
        v-model="showViewDialog"
        title="任务详情"
        width="800px"
      >
        <div v-if="currentTask">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="任务标题">
              {{ currentTask.taskTitle }}
            </el-descriptions-item>
            <el-descriptions-item label="所属课程">
              {{ currentTask.courseName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">
              {{ currentTask.startDate || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="截止时间">
              {{ currentTask.dueDate || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="满分">
              {{ currentTask.maxScore }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentTask.status)">
                {{ getStatusText(currentTask.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 20px;">
            <h4>任务描述</h4>
            <p>{{ currentTask.taskDescription || '无描述' }}</p>
          </div>

          <div style="margin-top: 20px;">
            <h4>具体要求</h4>
            <p>{{ currentTask.requirements || '无具体要求' }}</p>
          </div>
        </div>
        <template #footer>
          <el-button @click="showViewDialog = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 任务列表 -->
      <el-card>
        <el-table :data="tasks" style="width: 100%" v-loading="loading">
          <el-table-column prop="taskTitle" label="任务标题" />
          <el-table-column label="课程" width="150">
            <template #default="scope">
              {{ scope.row.courseName || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始时间" width="120" />
          <el-table-column prop="dueDate" label="截止时间" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240">
            <template #default="scope">
              <el-button class="action-btn view-btn" size="small" @click="viewTask(scope.row)">查看</el-button>
              <el-button
                v-if="(userStore.isTeacher || userStore.isAdmin) && scope.row.status === 'DRAFT'"
                class="action-btn publish-btn"
                size="small"
                @click="handlePublish(scope.row)"
              >
                发布
              </el-button>
              <el-button
                v-if="(userStore.isTeacher || userStore.isAdmin) && scope.row.status === 'DRAFT'"
                class="action-btn edit-btn"
                size="small"
                @click="editTask(scope.row)"
              >
                编辑
              </el-button>
              <el-button class="action-btn delete-btn" size="small" @click="deleteTask(scope.row)" v-if="userStore.isTeacher || userStore.isAdmin">删除</el-button>
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

      <!-- 创建/编辑任务对话框 -->
      <el-dialog
        v-model="showCreateDialog"
        :title="editingTask ? '编辑任务' : '创建任务'"
        width="600px"
        @close="resetForm"
      >
        <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="100px">
          <el-form-item label="所属课程" prop="courseId">
            <el-select v-model="taskForm.courseId" placeholder="请选择课程" style="width: 100%">
              <el-option
                v-for="item in courseList"
                :key="item.courseId"
                :label="item.courseName"
                :value="item.courseId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="任务标题" prop="taskTitle">
            <el-input v-model="taskForm.taskTitle" placeholder="请输入任务标题" />
          </el-form-item>
          <el-form-item label="任务描述" prop="taskDescription">
            <el-input
              v-model="taskForm.taskDescription"
              type="textarea"
              :rows="4"
              placeholder="请输入任务描述"
            />
          </el-form-item>
          <el-form-item label="具体要求">
            <el-input
              v-model="taskForm.requirements"
              type="textarea"
              :rows="4"
              placeholder="请输入具体要求"
            />
          </el-form-item>
          <el-form-item label="开始时间" prop="startDate">
            <el-date-picker
              v-model="taskForm.startDate"
              type="date"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="截止时间" prop="dueDate">
            <el-date-picker
              v-model="taskForm.dueDate"
              type="date"
              placeholder="选择截止时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="满分" prop="maxScore">
            <el-input-number v-model="taskForm.maxScore" :min="1" :max="1000" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getTaskList, createTask, deleteTask as deleteTaskApi, publishTask as publishTaskApi } from '@/api/task'
import { getMyCourses } from '@/api/course'
import Layout from '@/components/Layout.vue'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const showCreateDialog = ref(false)
const showViewDialog = ref(false)
const currentTask = ref(null)
const editingTask = ref(null)
const taskFormRef = ref()

const courseList = ref([]) // 课程列表

const searchForm = reactive({
  title: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const tasks = ref([])

const taskForm = reactive({
  taskId: null,      // 任务ID（编辑时使用）
  courseId: '',      // 课程ID
  taskTitle: '',
  taskDescription: '',
  requirements: '',
  startDate: '',
  dueDate: '',
  maxScore: 100,
  status: null       // 状态：DRAFT / PUBLISHED / CLOSED
})

const taskRules = {
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  taskTitle: [
    { required: true, message: '请输入任务标题', trigger: 'blur' }
  ],
  taskDescription: [
    { required: true, message: '请输入任务描述', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  dueDate: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ],
  maxScore: [
    { required: true, message: '请输入满分', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'CLOSED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'CLOSED': '已关闭'
  }
  return textMap[status] || status
}

const handleSearch = () => {
  pagination.page = 1
  loadTasks()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = ''
  pagination.page = 1
  loadTasks()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadTasks()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadTasks()
}

const loadTasks = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size,
      status: searchForm.status
    }
    const response = await getTaskList(params)
    if (response.code === 200) {
      tasks.value = attachCourseName(response.data.records)
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const handlePublish = async (task) => {
  try {
    await ElMessageBox.confirm('确定要发布这个任务吗？发布后学生将可以看到该任务。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await publishTaskApi(task.taskId)
    if (res.code === 200) {
      ElMessage.success('任务已发布')
      loadTasks()
    } else if (res.message) {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 用户取消或发布失败
  }
}

// 根据 courseId 补充课程名称
const attachCourseName = (records) => {
  if (!Array.isArray(records)) return []
  const map = new Map(courseList.value.map(c => [c.courseId, c.courseName]))
  return records.map(item => ({
    ...item,
    courseName: item.courseName || map.get(item.courseId) || ''
  }))
}

const loadCourses = async () => {
  try {
    const res = await getMyCourses()
    if (res.code === 200) {
      courseList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const viewTask = (task) => {
  currentTask.value = { ...task }
  showViewDialog.value = true
}

const editTask = (task) => {
  // 填充表单，进入编辑模式
  editingTask.value = { ...task }
  Object.assign(taskForm, {
    taskId: task.taskId,
    courseId: task.courseId,
    taskTitle: task.taskTitle,
    taskDescription: task.taskDescription,
    requirements: task.requirements,
    startDate: task.startDate,
    dueDate: task.dueDate,
    maxScore: task.maxScore,
    status: task.status
  })
  showCreateDialog.value = true
}

const deleteTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteTaskApi(task.taskId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadTasks()
    }
  } catch (error) {
    // 用户取消操作或删除失败
  }
}

const handleSubmit = async () => {
  if (!taskFormRef.value) return
  
  await taskFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await createTask(taskForm)
        if (res.code === 200) {
          ElMessage.success('任务创建成功')
          showCreateDialog.value = false
          loadTasks()
        }
      } catch (error) {
        // ElMessage.error('操作失败') 
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetForm = () => {
  editingTask.value = null
  Object.assign(taskForm, {
    taskId: null,
    taskTitle: '',
    taskDescription: '',
    requirements: '',
    startDate: '',
    dueDate: '',
    maxScore: 100,
    courseId: '',
    status: null
  })
  if (taskFormRef.value) {
    taskFormRef.value.resetFields()
  }
}

const handleCreate = () => {
  resetForm()
  showCreateDialog.value = true
}

onMounted(async () => {
  await loadCourses()
  await loadTasks()
})
</script>

<style scoped>
.tasks {
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
.create-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

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

.publish-btn {
  background: linear-gradient(135deg, #96fbc4, #f9f047);
  color: #333;
}

.publish-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(150, 251, 196, 0.4);
}

.edit-btn {
  background: linear-gradient(135deg, #ffecd2, #fcb69f);
  color: #333;
}

.edit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 236, 210, 0.4);
}

.delete-btn {
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  color: #333;
}

.delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 154, 158, 0.4);
}
</style>
