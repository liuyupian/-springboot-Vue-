<template>
  <Layout>
    <div class="profile">
      <el-row :gutter="20">
        <!-- 个人信息卡片 -->
        <el-col :span="8">
          <el-card class="profile-card">
            <div class="profile-header">
              <el-avatar :size="80">
                {{ userStore.user?.realName?.charAt(0) }}
              </el-avatar>
              <h3>{{ userStore.user?.realName }}</h3>
              <p class="user-type">{{ getUserTypeText(userStore.user?.userType) }}</p>
            </div>
            
            <div class="profile-info">
              <div class="info-item">
                <span class="label">用户名：</span>
                <span class="value">{{ userStore.user?.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">邮箱：</span>
                <span class="value">{{ userStore.user?.email || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">手机：</span>
                <span class="value">{{ userStore.user?.phone || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="label">最后登录：</span>
                <span class="value">{{ formatDate(userStore.user?.lastLoginAt) }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 编辑信息表单 -->
        <el-col :span="16">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>个人信息</span>
              </div>
            </template>
            
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              
              <!-- 学生特有信息 -->
              <template v-if="userStore.isStudent">
                <el-form-item label="学号">
                  <el-input v-model="studentInfo.studentNumber" disabled />
                </el-form-item>
                <el-form-item label="班级">
                  <el-input v-model="studentInfo.className" disabled />
                </el-form-item>
                <el-form-item label="专业">
                  <el-input v-model="studentInfo.majorName" disabled />
                </el-form-item>
                <el-form-item label="年级">
                  <el-input v-model="studentInfo.grade" disabled />
                </el-form-item>
              </template>
              
              <!-- 教师特有信息 -->
              <template v-if="userStore.isTeacher">
                <el-form-item label="工号">
                  <el-input v-model="teacherInfo.teacherNumber" disabled />
                </el-form-item>
                <el-form-item label="院系">
                  <el-input v-model="teacherInfo.deptName" disabled />
                </el-form-item>
                <el-form-item label="职称" prop="title">
                  <el-input v-model="teacherInfo.title" placeholder="请输入职称" />
                </el-form-item>
                <el-form-item label="研究方向" prop="researchArea">
                  <el-input
                    v-model="teacherInfo.researchArea"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入研究方向"
                  />
                </el-form-item>
                <el-form-item label="办公室" prop="officeLocation">
                  <el-input v-model="teacherInfo.officeLocation" placeholder="请输入办公室位置" />
                </el-form-item>
              </template>
              
              <el-form-item>
                <el-button class="save-btn" @click="handleUpdateProfile" :loading="updating">保存修改</el-button>
                <el-button class="reset-btn" @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

      <!-- 修改密码卡片 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>修改密码</span>
              </div>
            </template>
            
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="password-form"
              style="max-width: 500px;"
            >
              <el-form-item label="当前密码" prop="currentPassword">
                <el-input
                  v-model="passwordForm.currentPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item>
                <el-button class="change-password-btn" @click="handleChangePassword" :loading="changingPassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserProfile, updateProfile, changePassword } from '@/api/profile'
import Layout from '@/components/Layout.vue'

const userStore = useUserStore()

const profileFormRef = ref()
const passwordFormRef = ref()
const updating = ref(false)
const changingPassword = ref(false)

const profileForm = reactive({
  realName: '',
  email: '',
  phone: ''
})

const studentInfo = reactive({
  studentNumber: '',
  className: '',
  majorName: '',
  grade: ''
})

const teacherInfo = reactive({
  teacherNumber: '',
  deptName: '',
  title: '',
  researchArea: '',
  officeLocation: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const getUserTypeText = (userType) => {
  const typeMap = {
    'STUDENT': '学生',
    'TEACHER': '教师',
    'ADMIN': '管理员'
  }
  return typeMap[userType] || userType
}

const formatDate = (dateString) => {
  if (!dateString) return '从未登录'
  return new Date(dateString).toLocaleString()
}

const loadUserInfo = async () => {
  try {
    const res = await getUserProfile()
    if (res.code === 200) {
      const { user, student, teacher } = res.data
      
      // 更新用户基本信息
      Object.assign(profileForm, {
        realName: user.realName || '',
        email: user.email || '',
        phone: user.phone || ''
      })
      
      // 更新userStore中的用户信息
      Object.assign(userStore.user, user)
      
      // 根据用户类型加载详细信息
      if (student) {
        Object.assign(studentInfo, {
          studentNumber: student.studentNumber || '',
          className: student.className || '未设置',
          majorName: student.majorName || '未设置',
          grade: student.grade || '未设置'
        })
      }
      
      if (teacher) {
        Object.assign(teacherInfo, {
          teacherNumber: teacher.teacherNumber || '',
          deptName: teacher.deptName || '未设置',
          title: teacher.title || '',
          researchArea: teacher.researchArea || '',
          officeLocation: teacher.officeLocation || ''
        })
      }
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      updating.value = true
      try {
        const updateData = {
          realName: profileForm.realName,
          email: profileForm.email,
          phone: profileForm.phone
        }
        
        // 如果是教师，添加教师信息
        if (userStore.isTeacher) {
          updateData.teacher = {
            title: teacherInfo.title,
            researchArea: teacherInfo.researchArea,
            officeLocation: teacherInfo.officeLocation
          }
        }
        
        const res = await updateProfile(updateData)
        if (res.code === 200) {
          ElMessage.success('信息更新成功')
          // 更新store中的用户信息
          Object.assign(userStore.user, profileForm)
        } else {
          ElMessage.error(res.message || '信息更新失败')
        }
      } catch (error) {
        ElMessage.error('信息更新失败')
      } finally {
        updating.value = false
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        const passwordData = {
          currentPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        }
        
        const res = await changePassword(passwordData)
        if (res.code === 200) {
          ElMessage.success('密码修改成功')
          // 重置表单
          passwordForm.currentPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
        } else {
          ElMessage.error(res.message || '密码修改失败')
        }
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

const resetForm = () => {
  Object.assign(profileForm, {
    realName: userStore.user?.realName || '',
    email: userStore.user?.email || '',
    phone: userStore.user?.phone || ''
  })
}


onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile {
  padding: 0;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.profile-header h3 {
  margin: 15px 0 5px 0;
  color: #333;
}

.user-type {
  color: #666;
  margin: 0 0 15px 0;
  font-size: 14px;
}

.profile-info {
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #666;
  font-weight: 500;
}

.value {
  color: #333;
  font-weight: 500;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-form,
.password-form {
  margin-top: 20px;
}

/* 现代化按钮样式 */
.save-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  color: white;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px 20px;
  color: #666;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background: #e9e9e9;
  border-color: #ccc;
  transform: translateY(-1px);
}

.change-password-btn {
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  color: #333;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
  transition: all 0.3s ease;
}

.change-password-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
}
</style>
