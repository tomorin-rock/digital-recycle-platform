// 用户相关类型
export interface User {
  id: number
  username: string
  nickname: string
  avatar: string | null
  role: 'USER' | 'ADMIN'
  status: number
  createTime: string
  updateTime: string
  balance: number
}

export interface LoginDTO {
  username: string
  password: string
}

export interface RegisterDTO {
  username: string
  password: string
  confirmPwd: string
  nickname: string
}

export interface LoginResponse {
  token: string
  userInfo: User
}

// 产品相关类型
export interface Product {
  id: number
  categoryId: number
  brandId: number
  modelName: string
  description: string
  isNew: number
  marketPrice: number
  recycleBasePrice: number
  stock: number
  status: number
  imgUrl: string | null
  createTime: string
  updateTime: string
}

export interface ProductBrand {
  id: number
  name: string
  logo: string | null
  status: number
  sort: number
}

export interface ProductCategory {
  id: number
  name: string
  parentId: number | null
  status: number
  sort: number
}

// 回收订单类型
export interface RecycleOrder {
  id: number
  orderNo: string
  userId: number
  productId: number
  addressId: number
  productSnapshot: string
  grade: string
  appearanceDesc: string
  functionDesc: string
  estimatePrice: number
  finalPrice: number | null
  status: string
  createTime: string
  updateTime: string
}

export interface EstimateDTO {
  productId: number
  grade: string
}

export interface RecycleDTO {
  productId: number
  addressId: number
  grade: string
  appearanceDesc: string
  functionDesc: string
  estimatePrice: number
}

// 换新订单类型
export interface ExchangeOrder {
  id: number
  orderNo: string
  userId: number
  recycleId: number
  couponId: number | null
  newProductId: number
  addressId: number
  productPrice: number
  discountAmount: number
  payAmount: number
  status: string
  createTime: string
  updateTime: string
}

export interface ExchangeDTO {
  recycleId: number
  newProductId: number
  addressId: number
  couponId?: number
}

// 地址类型
export interface Address {
  id: number
  userId: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: number
  createTime: string
  updateTime: string
}

export interface AddressDTO {
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detail: string
}

// 论坛帖子类型
export interface ForumPost {
  id: number
  userId: number
  title: string
  content: string
  viewCount: number
  replyCount: number
  likeCount: number
  status: number
  createTime: string
  updateTime: string
  nickname?: string
}

export interface ForumComment {
  id: number
  postId: number
  userId: number
  parentId: number | null
  content: string
  likeCount: number
  createTime: string
  nickname?: string
}

// 优惠券类型
export interface Coupon {
  id: number
  userId: number
  sourceRecycleId: number
  code: string
  amount: number
  status: 'UNUSED' | 'USED' | 'EXPIRED'
  expireTime: string
  createTime: string
  useTime: string | null
}

// 操作日志类型
export interface OperationLog {
  id: number
  orderType: string
  orderId: number
  operatorId: number
  operateRole: string
  action: string
  detail: string
  createTime: string
}

// API 响应类型
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 分页响应
export interface PageResponse<T> {
  total: number
  list: T[]
  pageNum: number
  pageSize: number
}