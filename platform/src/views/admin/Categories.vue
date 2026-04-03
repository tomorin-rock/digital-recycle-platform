<template>
  <div class="admin-categories">
    <div class="page-header">
      <h1>分类管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加分类
      </el-button>
    </div>

    <div class="content-card">
      <el-table :data="categories" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="parentId" label="父分类ID" width="100" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editCategory(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadCategories"
          style="margin-top: 20px; justify-content: flex-end;"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '添加分类'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="请选择父分类">
            <el-option label="顶级分类" :value="null" />
            <el-option
                v-for="category in parentCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { adminCategoryApi } from '@/api/admin'
import type { ProductCategory } from '@/types'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus/es/components/form'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)

const categories = ref<ProductCategory[]>([])
const parentCategories = ref<ProductCategory[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const formRef = ref<FormInstance>()

const form = reactive({
  name: '',
  sort: 0,
  status: 1,
  parentId: null as number | null,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

onMounted(async () => {
  await loadCategories()
  await loadParentCategories()
})

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await adminCategoryApi.getList(pageNum.value, pageSize.value)
    // 后端返回的是分页对象，需要访问list属性获取分类数组
    categories.value = res.data?.list || []
    total.value = res.data?.total || 0
    pageNum.value = res.data?.pageNum || 1
    pageSize.value = res.data?.pageSize || 10
  } catch (error) {
    console.error('Failed to load categories:', error)
  } finally {
    loading.value = false
  }
}

const loadParentCategories = async () => {
  try {
    const res = await adminCategoryApi.getList(1, 100)
    parentCategories.value = res.data?.list || []
  } catch (error) {
    console.error('Failed to load parent categories:', error)
  }
}

const showAddDialog = () => {
  isEdit.value = false
  editId.value = null
  form.name = ''
  form.sort = 0
  form.status = 1
  form.parentId = null
  dialogVisible.value = true
}

const editCategory = (category: ProductCategory) => {
  isEdit.value = true
  editId.value = category.id
  form.name = category.name
  form.sort = category.sort
  form.status = category.status
  form.parentId = category.parentId
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value && editId.value) {
          // 确保请求体中包含 id 字段，并且与路径参数一致
          const updateData = { ...form, id: editId.value }
          await adminCategoryApi.update(editId.value, updateData)
          ElMessage.success('分类更新成功')
        } else {
          await adminCategoryApi.add(form)
          ElMessage.success('分类添加成功')
        }
        dialogVisible.value = false
        await loadCategories()
        await loadParentCategories()
      } catch (error) {
        console.error('Failed to submit:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.admin-categories { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h1 { font-size: 24px; color: #303133; }
.content-card { background: #fff; border-radius: 12px; padding: 20px; }
</style>