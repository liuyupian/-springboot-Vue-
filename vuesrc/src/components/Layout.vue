<template>
  <el-container class="layout-container">
    <!-- 顶部导航 -->
    <el-header class="layout-header">
      <div class="header-left">
        <div class="logo-section">
          <div class="logo-icon">
            <el-icon :size="28"><Document /></el-icon>
          </div>
          <h3>课程设计报告管理系统</h3>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.user?.avatarUrl">
              {{ userStore.user?.realName?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.user?.realName }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边菜单 -->
      <el-aside width="220px" class="layout-aside">
        <div class="menu-header">
          <div class="user-avatar">
            <el-avatar :size="50" :src="userStore.user?.avatarUrl">
              {{ userStore.user?.realName?.charAt(0) }}
            </el-avatar>
          </div>
          <div class="user-details">
            <div class="user-name">{{ userStore.user?.realName }}</div>
            <div class="user-role">{{ getUserRoleText() }}</div>
          </div>
        </div>
        <el-menu
          :default-active="$route.path"
          router
          class="layout-menu"
          background-color="transparent"
          text-color="rgba(255, 255, 255, 0.8)"
          active-text-color="#ffffff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          
          <el-menu-item index="/tasks" v-if="userStore.isTeacher || userStore.isAdmin">
            <el-icon><Document /></el-icon>
            <span>任务管理</span>
          </el-menu-item>
          
          <el-menu-item index="/submissions">
            <el-icon><Upload /></el-icon>
            <span>{{ userStore.isStudent ? '我的提交' : '提交管理' }}</span>
          </el-menu-item>
          
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <div class="main-content">
          <slot />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const getUserRoleText = () => {
  const typeMap = {
    'STUDENT': '学生',
    'TEACHER': '教师',
    'ADMIN': '管理员'
  }
  return typeMap[userStore.user?.userType] || '用户'
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.layout-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  position: relative;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.header-left h3 {
  color: #333;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: transparent;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: translateY(-1px);
}

.username {
  margin: 0 12px;
  color: #333;
  font-weight: 500;
}

.layout-aside {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 50;
}

.menu-header {
  padding: 30px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 20px;
}

.user-avatar {
  margin-bottom: 15px;
}

.user-avatar :deep(.el-avatar) {
  border: 3px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.user-details {
  color: white;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 5px;
}

.user-role {
  font-size: 12px;
  opacity: 0.8;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 12px;
  border-radius: 20px;
  display: inline-block;
}

.layout-menu {
  border-right: none;
  background: transparent;
  padding: 0 15px;
}

.layout-menu :deep(.el-menu-item) {
  border-radius: 12px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.layout-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

.layout-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transform: translateX(5px);
}

.layout-menu :deep(.el-menu-item .el-icon) {
  margin-right: 12px;
  font-size: 18px;
}

.layout-main {
  background: transparent;
  padding: 0;
  position: relative;
}

.main-content {
  padding: 30px;
  min-height: calc(100vh - 60px);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 20px 0 0 0;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.main-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23000" opacity="0.02"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>') repeat;
  pointer-events: none;
  z-index: 1;
}

.main-content > :deep(*) {
  position: relative;
  z-index: 2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-aside {
    width: 180px !important;
  }
  
  .menu-header {
    padding: 20px 15px;
  }
  
  .layout-menu {
    padding: 0 10px;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .header-left h3 {
    font-size: 16px;
  }
}
</style>
