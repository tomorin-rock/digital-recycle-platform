<template>
  <div class="recycle-orders-page">
    <div class="page-header">
      <h1>我的回收订单</h1>
      <p>查看和管理您的回收订单</p>
    </div>
    
    <div class="order-list" v-loading="loading">
      <el-empty v-if="orders.length === 0 && !loading" description="暂无回收订单">
        <el-button type="primary" @click="$router.push('/recycle')">立即回收</el-button>
      </el-empty>
      
      <div v-else class="orders">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <el-tag :type="getStatusType(order.status)" size="small">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
            <div class="order-time">
              {{ formatDate(order.createTime) }}
            </div>
          </div>
          
          <div class="order-body">
            <div class="product-info">
              <div class="product-name">{{ order.productSnapshot }}</div>
              <div class="product-grade">成色: {{ order.grade }}级</div>
              <div class="product-desc" v-if="order.appearanceDesc">
                外观: {{ order.appearanceDesc }}
              </div>
              <div class="product-desc" v-if="order.functionDesc">
                功能: {{ order.functionDesc }}
              </div>
            </div>
            
            <div class="price-info">
              <div class="price-item">
                <span class="label">预估价格</span>
                <span class="value">¥{{ order.estimatePrice }}</span>
              </div>
              <div class="price-item" v-if="order.finalPrice">
                <span class="label">最终价格</span>
                <span class="value final">¥{{ order.finalPrice }}</span>
              </div>
            </div>
          </div>
          
          <div class="order-footer">
            <el-button
              v-if="order.status === 'WAIT_CONFIRM'"
              type="success"
              size="small"
              @click="confirmOrder(order.id)"
            >
              确认收款
            </el-button>
            <el-button
              v-if="order.status === 'SUBMITTED'"
              type="danger"
              size="small"
              @click="cancelOrder(order.id)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="['SUBMITTED', 'INSPECTING'].includes(order.status)"
              type="primary"
              size="small"
              plain
              @click="goExchange(order.id)"
            >
              以旧换新
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { recycleApi } from '@/api/order'
import type { RecycleOrder } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const orders = ref<RecycleOrder[]>([])

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await recycleApi.getMyList()
    orders.value = res.data
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    SUBMITTED: 'info',
    INSPECTING: 'warning',
    WAIT_CONFIRM: 'info',
    FINISHED: 'success',
    CANCELLED: 'danger',
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    SUBMITTED: '已提交',
    INSPECTING: '质检中',
    WAIT_CONFIRM: '待确认',
    FINISHED: '已完成',
    CANCELLED: '已取消',
  }
  return map[status] || status
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const confirmOrder = async (id: number) => {
  try {
    // 弹出选择支付方式的对话框
    const {  } = await ElMessageBox.confirm(
        `<div style="padding: 20px;">
        <p style="margin-bottom: 16px;">请选择收款方式：</p>
        <div style="display: flex; gap: 20px;">
          <label style="display: flex; align-items: center; cursor: pointer;">
            <input type="radio" name="payType" value="CASH" checked style="margin-right: 8px;" />
            <span>收入余额</span>
          </label>
          <label style="display: flex; align-items: center; cursor: pointer;">
            <input type="radio" name="payType" value="COUPON" style="margin-right: 8px;" />
            <span>换成优惠券</span>
          </label>
        </div>
      </div>`,
      '选择收款方式',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
        dangerouslyUseHTMLString: true
      }
    )

    // 获取选中的支付方式
    const payType = document.querySelector('input[name="payType"]:checked')as HTMLInputElement | null
    const selectedPayType = payType?.value
    await recycleApi.confirm(id, selectedPayType)
    ElMessage.success('确认成功')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to confirm:', error)
    }
  }
}

const cancelOrder = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await recycleApi.cancel(id)
    ElMessage.success('订单已取消')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel:', error)
    }
  }
}

const goExchange = (recycleId: number) => {
  router.push({
    path: '/exchange',
    query: { recycleId }
  })
}
</script>

<style scoped>
.recycle-orders-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  border-radius: 12px;
  padding: 40px;
  color: #fff;
  text-align: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.order-list {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  min-height: 400px;
}

.order-card {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
}

.order-card:last-child {
  margin-bottom: 0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-no {
  font-weight: 500;
  color: #303133;
}

.order-time {
  color: #909399;
  font-size: 14px;
}

.order-body {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.product-grade {
  color: #606266;
  margin-bottom: 4px;
}

.product-desc {
  color: #909399;
  font-size: 13px;
  margin-bottom: 4px;
}

.price-info {
  text-align: right;
}

.price-item {
  margin-bottom: 8px;
}

.price-item .label {
  color: #909399;
  font-size: 12px;
  display: block;
}

.price-item .value {
  font-size: 18px;
  font-weight: 600;
  color: #606266;
}

.price-item .value.final {
  color: #f56c6c;
}

.order-footer {
  padding: 16px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 支付方式对话框样式 */
:deep input[type="radio"] {
  margin-right: 8px;
}

:deep label {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 20px;
}

:deep label:hover {
  color: #409EFF;
}
</style>