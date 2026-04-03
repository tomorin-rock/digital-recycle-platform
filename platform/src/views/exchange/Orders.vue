<template>
  <div class="exchange-orders-page">
    <div class="page-header">
      <h1>我的换新订单</h1>
      <p>查看和管理您的换新订单</p>
    </div>
    
    <div class="order-list" v-loading="loading">
      <el-empty v-if="orders.length === 0 && !loading" description="暂无换新订单">
        <el-button type="primary" @click="$router.push('/exchange')">立即换新</el-button>
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
            <div class="price-info">
              <div class="price-item">
                <span class="label">产品价格</span>
                <span class="value">¥{{ order.productPrice }}</span>
              </div>
              <div class="price-item">
                <span class="label">优惠金额</span>
                <span class="value discount">-¥{{ order.discountAmount }}</span>
              </div>
              <div class="price-item">
                <span class="label">实付金额</span>
                <span class="value pay">¥{{ order.payAmount }}</span>
              </div>
            </div>
          </div>
          
          <div class="order-footer">
            <el-button
              v-if="order.status === 'UNPAID'"
              type="primary"
              size="small"
              @click="payOrder(order.orderNo)"
            >
              立即支付
            </el-button>
            <el-button
              v-if="order.status === 'UNPAID'"
              type="danger"
              size="small"
              @click="cancelOrder(order.orderNo)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="order.status === 'SHIPPED'"
              type="success"
              size="small"
              @click="confirmReceive(order.orderNo)"
            >
              确认收货
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { exchangeApi } from '@/api/order'
import type { ExchangeOrder } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref<ExchangeOrder[]>([])

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await exchangeApi.getMyList()
    orders.value = res.data
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: 'warning',
    PAID: 'info',
    SHIPPED: 'primary',
    FINISHED: 'success',
    CANCELLED: 'danger',
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: '待支付',
    PAID: '已支付',
    SHIPPED: '已发货',
    FINISHED: '已完成',
    CANCELLED: '已取消',
  }
  return map[status] || status
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const payOrder = async (orderNo: string) => {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '支付确认', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      type: 'info',
    })
    
    await exchangeApi.pay(orderNo)
    ElMessage.success('支付成功')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to pay:')
    }
  }
}

const cancelOrder = async (orderNo: string) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await exchangeApi.cancel(orderNo)
    ElMessage.success('订单已取消')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel:', error)
    }
  }
}

const confirmReceive = async (orderNo: string) => {
  try {
    await ElMessageBox.confirm('确认已收到货物？', '收货确认', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info',
    })

    await exchangeApi.confirm(orderNo)
    ElMessage.success('已确认收货')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to confirm:', error)
    }
  }
}
</script>

<style scoped>
.exchange-orders-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #67C23A 0%, #409EFF 100%);
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
  padding: 20px;
}

.price-info {
  display: flex;
  gap: 40px;
}

.price-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.price-item .label {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}

.price-item .value {
  font-size: 18px;
  font-weight: 600;
}

.price-item .value.discount {
  color: #67C23A;
}

.price-item .value.pay {
  color: #f56c6c;
  font-size: 22px;
}

.order-footer {
  padding: 16px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
