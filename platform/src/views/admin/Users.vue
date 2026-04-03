<template>
  <div class="admin-users">
    <div class="page-header">
      <h1>用户管理</h1>
    </div>
    
    <div class="content-card">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              text
              size="small"
              @click="changeStatus(row.id, 0)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              text
              size="small"
              @click="changeStatus(row.id, 1)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadUsers"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminUserApi } from '@/api/admin'
import type { User} from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const users = ref<User[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(async () => {
  await loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await adminUserApi.getList()
    // 后端返回的是分页对象，需要访问list属性获取用户数组
    users.value = res.data?.list || []
    total.value = res.data?.total || 0
    pageNum.value = res.data?.pageNum || 1
    pageSize.value = res.data?.pageSize || 10
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const changeStatus = async (id: number, status: number) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '启用' : '禁用'}该用户吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await adminUserApi.updateStatus(id, status)
    if(status === 1){
      ElMessage.success('用户已启用')
    }else if(status === 0){
      ElMessage.success('用户已禁用')
    }
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to update status:', error)
    }
  }
}
</script>

<style scoped>
.admin-users {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}
</style>