import {request} from './index'
import type {
    User,
    RecycleOrder,
    ExchangeOrder,
    Product,
    ProductBrand,
    ProductCategory,
    ForumPost,
    OperationLog,
    PageResponse
} from '@/types'

export const adminDashBoardApi = {
    // 获取统计数据
    getStats(){
        return request.get<{
            userCount: number
            recycleCount: number
            exchangeCount: number
            productCount: number
        }>('/admin/dashboard/count')
    }
}

// 管理员用户管理 API
export const adminUserApi = {
    // 获取所有用户
    getList(pageNum = 1, pageSize = 10) {
        return request.get<PageResponse<User>>('/admin/users', {
            params: {pageNum, pageSize},
        })
    },

    // 修改用户状态
    updateStatus(id: number, status: number) {
        return request.put<string>(`/admin/users/${id}/status`, null, {
            params: {status},
        })
    },
}

// 管理员回收订单 API
export const adminRecycleApi = {
    // 获取所有回收订单
    getList(pageNum = 1, pageSize = 10) {
        return request.get<PageResponse<RecycleOrder>>('/admin/recycles', {
            params: {pageNum, pageSize},
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
    // 获取所有换新订单
    getList(pageNum = 1, pageSize = 10) {
        return request.get<PageResponse<ExchangeOrder>>('/admin/exchanges', {
            params: {pageNum, pageSize},
        })
    },

    // 发货
    shipping(id: number) {
        return request.put<string>(`/admin/exchanges/shipping/${id}`)
    },
}

// 管理员产品管理 API
export const adminProductApi = {
    // 获取所有产品
    getList(pageNum = 1, pageSize = 10, modelName?: string) {
        return request.get<PageResponse<Product>>('/admin/products', {
            params: {pageNum, pageSize, modelName},
        })
    },

    // 添加产品
    add(data: {
        categoryId: number
        brandId: number
        modelName: string
        imgUrl: string
        description: string
        isNew: number
        marketPrice: number
        recycleBasePrice: number
        stock: number
        status: number
    }) {
        return request.post<Product>('/admin/products', data)
    },

    // 修改产品状态
    updateStatus(id: number, status: number) {
        return request.put<string>(`/admin/products/status/${id}`, null, {
            params: {status},
        })
    },

    // 修改产品信息
    update(id: number, data: {
        categoryId: number
        brandId: number
        modelName: string
        imgUrl: string
        description: string
        isNew: number
        marketPrice: number
        recycleBasePrice: number
        stock: number
        status: number
    }) {
        return request.put<string>(`/admin/products/${id}`, data)
    },
}

// 管理员品牌管理 API
export const adminBrandApi = {
    // 获取所有品牌
    getList(pageNum = 1, pageSize = 10) {
        return request.get<PageResponse<ProductBrand>>('/admin/brands', {
            params: {pageNum, pageSize},
        })
    },

    // 添加品牌
    add(data: { name: string; sort: number; status: number }) {
        return request.post<string>('/admin/brands', data)
    },

    // 修改品牌
    update(id: number, data: { name: string; sort: number; status: number }) {
        return request.put<string>(`/admin/brands/${id}`, data)
    },

    // 删除品牌
    delete(id: number) {
        return request.delete<string>(`/admin/brands/${id}`)
    },
}

// 管理员分类管理 API
export const adminCategoryApi = {
    // 获取所有分类
    getList(pageNum = 1, pageSize = 10) {
        return request.get<PageResponse<ProductCategory>>('/admin/categories', {
            params: {pageNum, pageSize},
        })
    },

    // 添加分类
    add(data: { name: string; parentId: number | null; status: number; sort: number }) {
        return request.post<string>('/admin/categories', data)
    },

    // 修改分类
    update(id: number, data: { name: string; parentId: number | null; status: number; sort: number }) {
        return request.put<string>(`/admin/categories/${id}`, data)
    },
}

// 管理员帖子管理 API
export const adminPostApi = {
    // 获取所有帖子
    getList() {
        return request.get<ForumPost[]>('/user/posts')
    },

    // 删除帖子
    delete(id: number) {
        return request.delete<string>(`/admin/posts/${id}`)
    },

    // 删除评论
    deleteComment(id: number) {
        return request.delete<string>(`/admin/comments/${id}`)
    },
}

// 操作日志 API
export const orderLogApi = {
    // 获取操作日志
    getList(pageNum = 1, pageSize = 10) {
        return request.get<PageResponse<OperationLog>>('/admin/orderLogs', {
            params: {pageNum, pageSize},
        })
    },
}