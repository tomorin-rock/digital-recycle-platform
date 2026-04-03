import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'
import router from '@/router'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  
  // 登录
  async function login(username: string, password: string) {
    try {
      const res = await authApi.login({ username, password })
      token.value = res.data.token
      userInfo.value = res.data.userInfo
      localStorage.setItem('token', res.data.token)
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      return false
    }
  }
  
  // 注册
  async function register(data: {
    username: string
    password: string
    confirmPwd: string
    nickname: string
  }) {
    try {
      await authApi.register(data)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      return false
    }
  }
  
  // 登出
  async function logout() {
    try {
      await authApi.logout()
    } catch (error) {
      // ignore
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    await router.push('/login')
  }
  
  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await userApi.getMe()
      userInfo.value = res.data
    } catch (error) {
      await logout()
    }
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchUserInfo,
  }
})
