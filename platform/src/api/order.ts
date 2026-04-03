import { request } from './index'
import type { RecycleOrder, RecycleDTO, EstimateDTO, ExchangeOrder, ExchangeDTO, OperationLog, PageResponse } from '@/types'

// 回收订单 API
export const recycleApi = {
  // 估算回收价格
  estimate(data: EstimateDTO) {
    return request.post<number>('/user/recycles/estimate', data)
  },
  
  // 提交回收订单
  submit(data: RecycleDTO) {
    return request.post<RecycleOrder>('/user/recycles', data)
  },
  
  // 确认回收订单
  confirm(id: number, payType?: string) {
    const params = payType ? { payType } : {}
    return request.post(`/user/recycles/confirm/${id}`, null, { params })
  },
  
  // 取消回收订单
  cancel(id: number) {
    return request.put<string>(`/user/recycles/cancel/${id}`)
  },
  
  // 查询用户回收订单
  getMyList() {
    return request.get<RecycleOrder[]>('/user/recycles')
  },
}

// 以旧换新 API
export const exchangeApi = {
  // 创建换新订单
  create(data: ExchangeDTO) {
    return request.post<ExchangeOrder>('/user/exchange', data)
  },
  
  // 支付订单
  pay(orderNo: string) {
    return request.post<string>(`/user/exchange/pay/${orderNo}`)
  },
  
  // 取消订单
  cancel(orderNo: string) {
    return request.put<string>('/user/exchange/cancel', null, {
      params: { orderNo },
    })
  },
  
  // 确认收货
  confirm(orderNo: string) {
    return request.put<string>('/user/exchange/confirm', null, {
      params: { orderNo },
    })
  },
  
  // 查询用户换新订单
  getMyList() {
    return request.get<ExchangeOrder[]>('/user/exchange')
  },
}

// 管理员回收订单 API
export const adminRecycleApi = {
  // 查询所有回收订单
  getList(pageNum = 1, pageSize = 10) {
    return request.get<PageResponse<RecycleOrder>>('/admin/recycles', {
      params: { pageNum, pageSize },
    })
  },
  
  // 开始质检
  startInspection(id: number) {
    return request.post<string>(`/admin/recycles/startInspection/${id}`)
  },
  
  // 完成质检
  finishInspection(id: number, data: { finalPrice: number; remark: string }) {
    return request.post<string>(`/admin/recycles/finishInspection/${id}`, data)
  },
}

// 管理员换新订单 API
export const adminExchangeApi = {
  // 查询所有换新订单
  getList() {
    return request.get<ExchangeOrder[]>('/admin/exchanges')
  },
  
  // 发货
  shipping(id: number) {
    return request.put<string>(`/admin/exchanges/shipping/${id}`)
  },
}

// 订单操作日志 API
export const orderLogApi = {
  // 查询操作日志
  getList() {
    return request.get<OperationLog[]>('/admin/orderLogs')
  },
}