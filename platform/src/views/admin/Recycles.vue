<template>
  <div class="admin-recycles">
    <div class="page-header">
      <h1>回收订单管理</h1>
    </div>
    
    <div class="content-card">
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" min-width="130" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="productSnapshot" label="产品" width="150" />
        <el-table-column prop="grade" label="成色" width="60" />
        <el-table-column prop="estimatePrice" label="预估价" width="100">
          <template #default="{ row }">¥{{ row.estimatePrice }}</template>
        </el-table-column>
        <el-table-column prop="finalPrice" label="最终价" width="100">
          <template #default="{ row }">
            {{ row.finalPrice ? `¥${row.finalPrice}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'SUBMITTED'"
              type="primary"
              text
              size="small"
              @click="showFinishDialog(row)"
            >
              开始质检
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
    
    <!-- 完成质检弹窗 -->
    <el-dialog v-model="finishDialogVisible" title="完成质检" width="400px">
      <el-form :model="finishForm" label-width="80px">
        <el-form-item label="最终价格">
          <el-input-number v-model="finishForm.finalPrice" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="finishForm.remark" type="textarea" :rows="3" placeholder="请输入质检备注" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="finishDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="finishInspection">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { adminRecycleApi } from '@/api/admin'
import type { RecycleOrder } from '@/types'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const finishDialogVisible = ref(false)
const currentOrderId = ref<number | null>(null)

const orders = ref<RecycleOrder[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const finishForm = reactive({
  finalPrice: 0,
  remark: '',
})

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await adminRecycleApi.getList(pageNum.value, pageSize.value)
    orders.value = res.data.list
    total.value = res.data.total
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
    INSPECTED: 'success',
    WAIT_CONFIRM: 'primary',
    FINISHED: 'success',
    CANCELLED: 'danger',
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    SUBMITTED: '已提交',
    INSPECTING: '质检中',
    INSPECTED: '已质检',
    WAIT_CONFIRM: '待确认',
    FINISHED: '已完成',
    CANCELLED: '已取消',
  }
  return map[status] || status
}

// 开始质检的方法已合并到finishInspection中

const showFinishDialog = (order: RecycleOrder) => {
  currentOrderId.value = order.id
  finishForm.finalPrice = order.estimatePrice
  finishForm.remark = ''
  finishDialogVisible.value = true
}

const finishInspection = async () => {
  if (!currentOrderId.value) return
  
  submitting.value = true
  try {
    // 先开始质检
    await adminRecycleApi.startInspection(currentOrderId.value)
    // 然后完成质检
    await adminRecycleApi.finishInspection(currentOrderId.value, finishForm)
    ElMessage.success('质检完成')
    finishDialogVisible.value = false
    await loadOrders()
  } catch (error) {
    console.error('Failed to finish inspection:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.admin-recycles { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 24px; color: #303133; }
.content-card { background: #fff; border-radius: 12px; padding: 20px; }
</style>