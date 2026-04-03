<template>
  <div class="admin-logs">
    <div class="page-header">
      <h1>操作日志</h1>
    </div>
    
    <div class="content-card">
      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderType" label="订单类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.orderType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="订单ID" width="80" />
        <el-table-column prop="action" label="操作" width="120" />
        <el-table-column prop="detail" label="详情" min-width="200" />
        <el-table-column prop="operateRole" label="操作角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.operateRole === 'ADMIN' ? 'danger' : 'primary'" size="small">
              {{ row.operateRole }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadLogs"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderLogApi } from '@/api/admin'
import type { OperationLog } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const logs = ref<OperationLog[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(async () => {
  await loadLogs()
})

const loadLogs = async () => {
  loading.value = true
  try {
    const res = await orderLogApi.getList(pageNum.value, pageSize.value)
    // 后端返回的是分页对象，需要访问list属性获取日志数组
    logs.value = res.data?.list || []
    total.value = res.data?.total || 0
    pageNum.value = res.data?.pageNum || 1
    pageSize.value = res.data?.pageSize || 10
  } catch (error) {
    console.error('Failed to load logs:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')
</script>

<style scoped>
.admin-logs { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; color: #303133; }
.content-card { background: #fff; border-radius: 12px; padding: 20px; }
</style>