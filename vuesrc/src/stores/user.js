import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, logout as apiLogout } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  const isAuthenticated = computed(() => !!token.value)
  const userType = computed(() => user.value?.userType || '')
  const isStudent = computed(() => userType.value === 'STUDENT')
  const isTeacher = computed(() => userType.value === 'TEACHER')
  const isAdmin = computed(() => userType.value === 'ADMIN')

  const login = async (credentials) => {
    try {
      const response = await apiLogin(credentials)
      if (response.code === 200) {
        token.value = response.data.token
        user.value = response.data.user
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  const logout = async () => {
    try {
      await apiLogout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      token.value = ''
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  const initializeAuth = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser && token.value) {
      try {
        user.value = JSON.parse(savedUser)
      } catch (error) {
        console.error('Failed to parse saved user:', error)
        logout()
      }
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    userType,
    isStudent,
    isTeacher,
    isAdmin,
    login,
    logout,
    initializeAuth
  }
})
