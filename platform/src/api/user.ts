import { request } from './index'
import type { User, Address, AddressDTO, Coupon } from '@/types'

// 用户相关 API
export const userApi = {
  // 获取当前用户信息
  getMe() {
    return request.get<User>('/user/me')
  },
  
  // 修改密码
  updatePassword(data: { oldPwd: string; newPwd: string; confirmPwd: string }) {
    return request.put('/user/me/password', data)
  },
  
  // 修改用户信息
  updateInfo(data: { nickname: string; amount: number }) {
    return request.put('/user/me', data)
  },
  
  // 上传头像
  uploadAvatar(file: File) {
    return request.upload<string>('/user/me/avatar', file)
  },
  
  // 注销账户
  deleteAccount() {
    return request.post('/user/me/cancel')
  },
}

// 地址管理 API
export const addressApi = {
  // 获取地址列表
  getList() {
    return request.get<Address[]>('/user/addresses')
  },
  
  // 添加地址
  add(data: AddressDTO) {
    return request.post<Address>('/user/addresses', data)
  },
  
  // 修改地址
  update(id: number, data: AddressDTO) {
    return request.put<string>(`/user/addresses/${id}`, data)
  },
  
  // 删除地址
  delete(id: number) {
    return request.delete<string>(`/user/addresses/${id}`)
  },
  
  // 设为默认地址
  setDefault(id: number) {
    return request.put<string>(`/user/addresses/default/${id}`)
  },
  
  // 获取默认地址
  getDefault() {
    return request.get<Address>('/user/addresses/default')
  },
}

// 优惠券 API
export const couponApi = {
  // 获取用户优惠券列表
    getList() {
    return request.get<Coupon[]>('/user/coupons')
  },
}
