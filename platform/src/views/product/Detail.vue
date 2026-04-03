<template>
  <div class="product-detail-page" v-loading="loading">
    <template v-if="product">
      <div class="product-main">
        <!-- 产品图片 -->
        <div class="product-image">
          <el-icon v-if="!product.imgUrl" class="placeholder-icon"><Iphone /></el-icon>
          <img v-else :src="product.imgUrl" :alt="product.modelName" />
        </div>
        
        <!-- 产品信息 -->
        <div class="product-info">
          <p class="model-name">型号: {{ product.modelName }}</p>
          <p class="description">{{ product.description }}</p>
          
          <div class="price-section">
            <div class="price-item">
              <span class="label">市场价</span>
              <span class="value market">¥{{ product.marketPrice }}</span>
            </div>
            <div class="price-item">
              <span class="label">回收价</span>
              <span class="value recycle">¥{{ product.recycleBasePrice }}起</span>
            </div>
          </div>
          
          <div class="info-row">
            <span class="label">库存:</span>
            <span class="value">{{ product.stock }}件</span>
          </div>
          
          <div class="info-row">
            <span class="label">状态:</span>
            <el-tag :type="product.status === 1 ? 'success' : 'info'">
              {{ product.status === 1 ? '上架中' : '已下架' }}
            </el-tag>
          </div>
          
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              :disabled="product.status !== 1"
              @click="goRecycle"
            >
              立即回收
            </el-button>
            <el-button
              type="success"
              size="large"
              :disabled="product.status !== 1 || product.stock <= 0"
              @click="goExchange"
            >
              以旧换新
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 产品详情说明 -->
      <div class="product-detail-section">
        <h2>产品详情</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产品ID">{{ product.id }}</el-descriptions-item>
          <el-descriptions-item label="型号名称">{{ product.modelName }}</el-descriptions-item>
          <el-descriptions-item label="市场价">¥{{ product.marketPrice }}</el-descriptions-item>
          <el-descriptions-item label="回收基础价">¥{{ product.recycleBasePrice }}</el-descriptions-item>
          <el-descriptions-item label="库存数量">{{ product.stock }}件</el-descriptions-item>
          <el-descriptions-item label="是否新品">
            <el-tag :type="product.isNew ? 'danger' : 'info'">
              {{ product.isNew ? '新品' : '二手' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(product.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(product.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </template>
    
    <el-empty v-else-if="!loading" description="产品不存在" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import type { Product } from '@/types'
import { Iphone } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const product = ref<Product | null>(null)

onMounted(async () => {
  const id = Number(route.params.id)
  if (id) {
    await loadProduct(id)
  }
})

const loadProduct = async (id: number) => {
  loading.value = true
  try {
    const res = await productApi.getDetail(id)
    product.value = res.data
  } catch (error) {
    console.error('Failed to load product:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const goRecycle = () => {
  router.push({
    path: '/recycle',
    query: { productId: product.value?.id }
  })
}

const goExchange = () => {
  router.push({
    path: '/exchange',
    query: { newProductId: product.value?.id }
  })
}
</script>

<style scoped>
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
}

.product-main {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 24px;
}

.product-image {
  width: 400px;
  height: 400px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 12px;
}

.placeholder-icon {
  font-size: 120px;
  color: #c0c4cc;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.product-info {
  flex: 1;
}

.product-info h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.model-name {
  color: #909399;
  margin-bottom: 16px;
}

.description {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 24px;
}

.price-section {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.price-item:last-child {
  margin-bottom: 0;
}

.price-item .label {
  color: #909399;
}

.price-item .value {
  font-size: 24px;
  font-weight: 600;
}

.price-item .value.market {
  color: #606266;
  text-decoration: line-through;
  font-size: 16px;
}

.price-item .value.recycle {
  color: #f56c6c;
}

.info-row {
  margin-bottom: 12px;
}

.info-row .label {
  color: #909399;
  margin-right: 8px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 30px;
}

.action-buttons .el-button {
  flex: 1;
  height: 48px;
  font-size: 16px;
}

.product-detail-section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
}

.product-detail-section h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #303133;
}

@media (max-width: 992px) {
  .product-main {
    flex-direction: column;
    align-items: center;
  }
  
  .product-image {
    width: 100%;
    max-width: 400px;
    height: 300px;
  }
}
</style>
