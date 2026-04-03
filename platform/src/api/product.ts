import { request } from './index'
import type { Product, ProductBrand, ProductCategory } from '@/types'

// 产品相关 API
export const productApi = {
  // 查询所有产品
  getList(modelName?: string) {
    const params = modelName ? { modelName } : {}
    return request.get<Product[]>('/user/products', { params })
  },
  
  // 查询产品详情
  getDetail(id: number) {
    return request.get<Product>(`/user/products/${id}`)
  },

  // 查询所有品牌
  getBrands() {
    return request.get<ProductBrand[]>('/user/products/brands')
  },

  // 查询所有分类
  getCategories() {
    return request.get<ProductCategory[]>('/user/products/categories')
  },

  // 按品牌查询产品
  getByBrand(brandId: number) {
    return request.get<Product[]>(`/user/products/brand/${brandId}`)
  },
  
  // 按分类查询产品
  getByCategory(parentId: number, categoryId?: number) {
    const params = categoryId ? { categoryId } : {}
    return request.get<{ categories: ProductCategory[]; products: Product[] }>(
      `/user/products/parent/${parentId}`,
      { params }
    )
  },
}