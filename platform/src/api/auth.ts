import { request } from './index'
import type { LoginDTO, RegisterDTO, LoginResponse, ApiResponse } from '@/types'

// 认证相关 API
export const authApi = {
  // 用户登录
  login(data: LoginDTO) {
    return request.post<LoginResponse>('/auth/login', data)
  },
  
  // 用户注册
  register(data: RegisterDTO) {
    return request.post<string>('/auth/register', data)
  },
  
  // 用户登出
  logout() {
    return request.post<string>('/auth/logout')
  },
}
