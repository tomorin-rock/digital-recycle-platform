<template>
  <div class="admin-exchanges">
    <div class="page-header">
      <h1>换新订单管理</h1>
    </div>
    
    <div class="content-card">
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" min-width="130" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="productPrice" label="产品价" width="100">
          <template #default="{ row }">¥{{ row.productPrice }}</template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠" width="100">
          <template #default="{ row }">¥{{ row.discountAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付" width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PAID'"
              type="primary"
              text
              size="small"
              @click="shipping(row.id)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadOrders"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminExchangeApi } from '@/api/admin'
import type { ExchangeOrder } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref<ExchangeOrder[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await adminExchangeApi.getList(pageNum.value, pageSize.value)
    // 后端返回的是分页对象，需要访问list属性获取订单数组
    orders.value = res.data?.list || []
    total.value = res.data?.total || 0
    pageNum.value = res.data?.pageNum || 1
    pageSize.value = res.data?.pageSize || 10
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

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')

const shipping = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要发货吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
    })
    
    await adminExchangeApi.shipping(id)
    ElMessage.success('发货成功')
    await loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to shipping:', error)
    }
  }
}
</script>

<style scoped>
.admin-exchanges { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; color: #303133; }
.content-card { background: #fff; border-radius: 12px; padding: 20px; }
</style>