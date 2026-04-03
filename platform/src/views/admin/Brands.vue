<template>
  <div class="admin-brands">
    <div class="page-header">
      <h1>品牌管理</h1>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加品牌
      </el-button>
    </div>

    <div class="content-card">
      <el-table :data="brands" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="品牌Logo" width="100">
          <template #default="{ row }">
            <el-image
                v-if="row.logo"
                :src="row.logo"
                :alt="row.name"
                fit="cover"
                style="width: 60px; height: 60px;"
            />
            <span v-else>无Logo</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="品牌名称" min-width="150" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editBrand(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="deleteBrand(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadBrands"
          style="margin-top: 20px; justify-content: flex-end;"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑品牌' : '添加品牌'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入品牌名称" />
        </el-form-item>

        <el-form-item label="品牌Logo">
          <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleLogoUpload"
              :before-upload="beforeUpload"
              :disabled="!editId"
              :headers="uploadHeaders"
              accept="image/*"
          >
            <img v-if="form.logo" :src="form.logo" :alt="form.name" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div v-if="!editId" class="upload-tip">
            请先保存品牌信息，然后再上传Logo
          </div>
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
import { adminBrandApi } from '@/api/admin'
import type { ProductBrand } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus/es/components/form'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const uploadUrl = ref('')
const uploadHeaders = ref({})

// 确保每次都获取最新的token
const updateUploadHeaders = () => {
  uploadHeaders.value = {
    Authorization: `Bearer ${localStorage.getItem('token') || ''}`,
  }
}

// 初始化时更新
updateUploadHeaders()

const brands = ref<ProductBrand[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const formRef = ref<FormInstance>()

const form = reactive({
  id: 0,
  name: '',
  logo: '',
  sort: 0,
  status: 1,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }],
}

onMounted(async () => {
  await loadBrands()
})

const loadBrands = async () => {
  loading.value = true
  try {
    const res = await adminBrandApi.getList(pageNum.value, pageSize.value)
    // 后端返回的是分页对象，需要访问list属性获取品牌数组
    brands.value = res.data?.list || []
    total.value = res.data?.total || 0
    pageNum.value = res.data?.pageNum || 1
    pageSize.value = res.data?.pageSize || 10
  } catch (error) {
    console.error('Failed to load brands:', error)
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  editId.value = null
  uploadUrl.value = `/api/admin/brands/${editId.value}/logo`
  form.id = 0
  form.name = ''
  form.logo = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

const editBrand = (brand: ProductBrand) => {
  isEdit.value = true
  editId.value = brand.id
  uploadUrl.value = `/api/admin/brands/${editId.value}/logo`
  form.id = brand.id
  form.name = brand.name
  form.logo = brand.logo || ''
  form.sort = brand.sort
  form.status = brand.status
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value && editId.value) {
          // 编辑模式：只更新非空字段
          const updateData = {
            id: editId.value,
            name: form.name,
            sort: form.sort,
            status: form.status,
            logo: form.logo
          }
          // 只有当logo不为空时才更新
          if (form.logo) {
            updateData.logo = form.logo
          }

          await adminBrandApi.update(editId.value, updateData)
          ElMessage.success('品牌更新成功')
          dialogVisible.value = false
          await loadBrands()
        } else {
          // 添加模式：先添加品牌信息（不包含logo）
          const addData = {
            name: form.name,
            sort: form.sort,
            status: form.status
          }

          const res = await adminBrandApi.add(addData)
          ElMessage.success('品牌添加成功')

          if (res.data && typeof res.data === 'object') {
            const data = res.data as any
            if (data.id) {
              editId.value = data.id
              isEdit.value = true
              uploadUrl.value = `/api/admin/brands/${editId.value}/logo`
              updateUploadHeaders()
              form.id = editId.value as number
              ElMessage.info('请上传品牌Logo')
            } else {
              dialogVisible.value = false
              await loadBrands()
            }
          } else {
            dialogVisible.value = false
            await loadBrands()
          }
        }
      } catch (error) {
        console.error('Failed to submit:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const deleteBrand = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该品牌吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await adminBrandApi.delete(id)
    ElMessage.success('品牌删除成功')
    await loadBrands()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete:', error)
    }
  }
}

// 处理Logo上传
const handleLogoUpload = async (response: any) => {
  form.logo = response.data.logo
  ElMessage.success('Logo上传成功')
}

// 上传前验证
const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传 JPG/PNG 图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}
</script>

<style scoped>
.admin-brands {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* Logo上传样式 */
.avatar-uploader el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color .3s;
}

.avatar-uploader el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>