<template>
  <div class="coupons-page">
    <div class="page-header">
      <h1>我的优惠券</h1>
      <p>查看您获得的优惠券</p>
    </div>
    
    <div class="coupons-content" v-loading="loading">
      <el-empty v-if="coupons.length === 0 && !loading" description="暂无优惠券">
        <template #description>
          <p>完成回收订单可获得优惠券</p>
        </template>
        <el-button type="primary" @click="$router.push('/recycle')">去回收</el-button>
      </el-empty>
      
      <div v-else class="coupon-list">
        <div
          v-for="coupon in coupons"
          :key="coupon.id"
          class="coupon-card"
          :class="{ expired: coupon.status === 'EXPIRED', used: coupon.status === 'USED' }"
        >
          <div class="coupon-left">
            <div class="coupon-amount">
              <span class="currency">¥</span>
              <span class="value">{{ coupon.amount }}</span>
            </div>
            <div class="coupon-type">满减券</div>
          </div>
          
          <div class="coupon-right">
            <div class="coupon-code">券码: {{ coupon.code }}</div>
            <div class="coupon-status">
              <el-tag :type="getStatusType(coupon.status)" size="small">
                {{ getStatusText(coupon.status) }}
              </el-tag>
            </div>
            <div class="coupon-expire">
              有效期至 {{ formatDate(coupon.expireTime) }}
            </div>
            <div class="coupon-source" v-if="coupon.sourceRecycleId">
              来源: 回收订单 #{{ coupon.sourceRecycleId }}
            </div>
            <div class="coupon-actions" v-if="coupon.status === 'UNUSED'">
              <el-button type="primary" size="small" @click="$router.push('/products')">去使用</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { couponApi } from '@/api/user'
import type { Coupon } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const coupons = ref<Coupon[]>([])

onMounted(async () => {
  await loadCoupons()
})

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await couponApi.getList()
    coupons.value = res.data
  } catch (error) {
    console.error('Failed to load coupons:', error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    UNUSED: 'success',
    USED: 'info',
    EXPIRED: 'danger',
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    UNUSED: '未使用',
    USED: '已使用',
    EXPIRED: '已过期',
  }
  return map[status] || status
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}
</script>

<style scoped>
.coupons-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
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

.coupons-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  min-height: 400px;
}

。coupon-action{
  margin-top: 16px;
}
.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coupon-card {
  display: flex;
  background: linear-gradient(135deg, #fff 0%, #f5f7fa 100%);
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid #E6A23C;
}

.coupon-card.expired {
  border-color: #c0c4cc;
  opacity: 0.7;
}

.coupon-card.used {
  border-color: #909399;
  opacity: 0.7;
}

.coupon-left {
  background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
  color: #fff;
  padding: 20px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 120px;
}

.coupon-card.expired .coupon-left,
.coupon-card.used .coupon-left {
  background: linear-gradient(135deg, #909399 0%, #c0c4cc 100%);
}

.coupon-amount .currency {
  font-size: 16px;
}

.coupon-amount .value {
  font-size: 36px;
  font-weight: 600;
}

.coupon-type {
  font-size: 12px;
  margin-top: 4px;
}

.coupon-right {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-code {
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.coupon-status {
  margin-bottom: 8px;
}

.coupon-expire {
  color: #909399;
  font-size: 13px;
  margin-bottom: 4px;
}

.coupon-source {
  color: #909399;
  font-size: 12px;
}
</style>
