<template>
  <Layout>
    <div class="dashboard">
      <el-row :gutter="20">
        <!-- 统计卡片 -->
        <el-col :span="6" v-for="stat in stats" :key="stat.title">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" :style="{ backgroundColor: stat.color }">
                <el-icon :size="24">
                  <component :is="stat.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-title">{{ stat.title }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 最近任务 -->
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>最近任务</span>
                <el-button type="text" @click="$router.push('/tasks')">查看更多</el-button>
              </div>
            </template>
            <el-table :data="recentTasks" style="width: 100%">
              <el-table-column prop="taskTitle" label="任务标题" />
              <el-table-column prop="deadline" label="截止时间" width="120">
                <template #default="scope">
                  {{ formatDate(scope.row.deadline) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <!-- 最近提交 -->
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>最近提交</span>
                <el-button type="text" @click="$router.push('/submissions')">查看更多</el-button>
              </div>
            </template>
            <el-table :data="recentSubmissions" style="width: 100%">
              <el-table-column prop="title" label="提交标题" />
              <el-table-column prop="submittedAt" label="提交时间" width="120">
                <template #default="scope">
                  {{ formatDate(scope.row.submittedAt) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getDashboardStats } from '@/api/dashboard'
import Layout from '@/components/Layout.vue'

const userStore = useUserStore()
const loading = ref(false)
const dashboardData = ref({})

const stats = computed(() => {
  const statsData = dashboardData.value.stats || {}
  
  if (userStore.isStudent) {
    return [
      { title: '总任务', value: statsData.totalTasks || 0, icon: 'Document', color: '#409EFF' },
      { title: '已提交', value: statsData.submittedCount || 0, icon: 'Upload', color: '#67C23A' },
      { title: '已完成', value: statsData.completedCount || 0, icon: 'Check', color: '#67C23A' },
      { title: '待提交', value: statsData.pendingCount || 0, icon: 'Clock', color: '#E6A23C' }
    ]
  } else if (userStore.isTeacher) {
    return [
      { title: '发布任务', value: statsData.publishedTasks || 0, icon: 'Document', color: '#409EFF' },
      { title: '指导学生', value: statsData.studentCount || 0, icon: 'User', color: '#909399' },
      { title: '待评审', value: statsData.pendingReviews || 0, icon: 'Clock', color: '#E6A23C' },
      { title: '已评审', value: statsData.completedReviews || 0, icon: 'Check', color: '#67C23A' }
    ]
  } else {
    return [
      { title: '总用户', value: statsData.totalUsers || 0, icon: 'User', color: '#409EFF' },
      { title: '总任务', value: statsData.totalTasks || 0, icon: 'Document', color: '#67C23A' },
      { title: '总提交', value: statsData.totalSubmissions || 0, icon: 'Upload', color: '#E6A23C' },
      { title: '总评审', value: statsData.totalReviews || 0, icon: 'Check', color: '#67C23A' }
    ]
  }
})

const recentTasks = computed(() => {
  return dashboardData.value.recentTasks || []
})

const recentSubmissions = computed(() => {
  return dashboardData.value.recentSubmissions || []
})

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'CLOSED': 'danger',
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
    'PUBLISHED': '已发布',
    'CLOSED': '已关闭',
    'SUBMITTED': '已提交',
    'UNDER_REVIEW': '评审中',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    '未提交': '未提交',
    '已提交': '已提交',
    '评审中': '评审中',
    '已通过': '已通过',
    '未通过': '未通过'
  }
  return textMap[status] || status
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}

const loadDashboardData = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    if (res.code === 200) {
      dashboardData.value = res.data
    } else {
      ElMessage.error(res.message || '获取Dashboard数据失败')
    }
  } catch (error) {
    ElMessage.error('获取Dashboard数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-title {
  font-size: 14px;
  color: #666;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
