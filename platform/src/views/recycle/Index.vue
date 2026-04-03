<template>
  <div class="recycle-page">
    <div class="page-header">
      <h1>设备回收</h1>
      <p>快速估价，高价回收您的闲置设备</p>
    </div>
    
    <el-steps :active="activeStep" finish-status="success" align-center class="steps">
      <el-step title="选择设备" />
      <el-step title="评估成色" />
      <el-step title="填写信息" />
      <el-step title="提交订单" />
    </el-steps>
    
    <!-- 步骤1: 选择设备 -->
    <div v-show="activeStep === 0" class="step-content">
      <div class="section-title">选择要回收的设备</div>
      
      <div class="product-select">
        <el-select
          v-model="selectedProductId"
          placeholder="请选择产品"
          filterable
          size="large"
          style="width: 100%"
          @change="handleProductChange"
        >
          <el-option
            v-for="product in products"
            :key="product.id"
            :label="`${product.modelName} - ${product.description}`"
            :value="product.id"
          >
            <div style="display: flex; justify-content: space-between;">
              <span>{{ product.modelName }}</span>
              <span style="color: #909399;">回收价 ¥{{ product.recycleBasePrice }}起</span>
            </div>
          </el-option>
        </el-select>
      </div>
      
      <div v-if="selectedProduct" class="selected-product">
        <h3>已选择产品</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产品名称">{{ selectedProduct.modelName }}</el-descriptions-item>
          <el-descriptions-item label="产品描述">{{ selectedProduct.description }}</el-descriptions-item>
          <el-descriptions-item label="市场价">¥{{ selectedProduct.marketPrice }}</el-descriptions-item>
          <el-descriptions-item label="回收基础价">¥{{ selectedProduct.recycleBasePrice }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div class="step-actions">
        <el-button type="primary" size="large" :disabled="!selectedProductId" @click="nextStep">
          下一步
        </el-button>
      </div>
    </div>
    
    <!-- 步骤2: 评估成色 -->
    <div v-show="activeStep === 1" class="step-content">
      <div class="section-title">评估设备成色</div>
      
      <el-form :model="gradeForm" label-width="100px" class="grade-form">
        <el-form-item label="设备成色">
          <el-radio-group v-model="gradeForm.grade">
            <el-radio value="A">A级 - 全新/准新（无使用痕迹）</el-radio>
            <el-radio value="B">B级 - 良好（轻微使用痕迹）</el-radio>
            <el-radio value="C">C级 - 一般（明显使用痕迹）</el-radio>
            <el-radio value="D">D级 - 较差（严重使用痕迹或轻微损坏）</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="外观描述">
          <el-input
            v-model="gradeForm.appearanceDesc"
            type="textarea"
            :rows="3"
            placeholder="请描述设备外观状况，如划痕、磕碰等"
          />
        </el-form-item>
        
        <el-form-item label="功能描述">
          <el-input
            v-model="gradeForm.functionDesc"
            type="textarea"
            :rows="3"
            placeholder="请描述设备功能状况，如是否正常使用、有无故障等"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="estimatePrice" :loading="estimating">
            获取估价
          </el-button>
        </el-form-item>
        
        <el-form-item v-if="estimatedPrice !== null" label="预估价格">
          <div class="estimated-price">
            <span class="price">¥{{ estimatedPrice }}</span>
            <span class="note">最终价格以质检结果为准</span>
          </div>
        </el-form-item>
      </el-form>
      
      <div class="step-actions">
        <el-button size="large" @click="prevStep">上一步</el-button>
        <el-button
          type="primary"
          size="large"
          :disabled="estimatedPrice === null"
          @click="nextStep"
        >
          下一步
        </el-button>
      </div>
    </div>
    
    <!-- 步骤3: 填写信息 -->
    <div v-show="activeStep === 2" class="step-content">
      <div class="section-title">填写收货地址</div>
      
      <div class="address-select">
        <el-radio-group v-model="selectedAddressId" v-if="addresses.length > 0">
          <el-radio
            v-for="address in addresses"
            :key="address.id"
            :value="address.id"
            class="address-item"
          >
            <div class="address-info">
              <div class="receiver">
                {{ address.receiverName }} {{ address.receiverPhone }}
                <el-tag v-if="address.isDefault" type="success" size="small">默认</el-tag>
              </div>
              <div class="detail">
                {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
              </div>
            </div>
          </el-radio>
        </el-radio-group>
        
        <el-empty v-else description="暂无地址，请先添加地址">
          <el-button type="primary" @click="$router.push('/addresses')">添加地址</el-button>
        </el-empty>
      </div>
      
      <div class="step-actions">
        <el-button size="large" @click="prevStep">上一步</el-button>
        <el-button
          type="primary"
          size="large"
          :disabled="!selectedAddressId"
          @click="nextStep"
        >
          下一步
        </el-button>
      </div>
    </div>
    
    <!-- 步骤4: 确认提交 -->
    <div v-show="activeStep === 3" class="step-content">
      <div class="section-title">确认订单信息</div>
      
      <div class="order-summary">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="产品名称">{{ selectedProduct?.modelName }}</el-descriptions-item>
          <el-descriptions-item label="设备成色">{{ gradeForm.grade }}级</el-descriptions-item>
          <el-descriptions-item label="预估价格">¥{{ estimatedPrice }}</el-descriptions-item>
          <el-descriptions-item label="外观描述">{{ gradeForm.appearanceDesc || '-' }}</el-descriptions-item>
          <el-descriptions-item label="功能描述">{{ gradeForm.functionDesc || '-' }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">
            {{ selectedAddress?.province }}{{ selectedAddress?.city }}{{ selectedAddress?.district }}{{ selectedAddress?.detail }}
            ({{ selectedAddress?.receiverName }} {{ selectedAddress?.receiverPhone }})
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div class="step-actions">
        <el-button size="large" @click="prevStep">上一步</el-button>
        <el-button type="primary" size="large" :loading="submitting" @click="submitOrder">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { recycleApi } from '@/api/order'
import { addressApi } from '@/api/user'
import type { Product, Address } from '@/types'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const activeStep = ref(0)
const products = ref<Product[]>([])
const addresses = ref<Address[]>([])
const selectedProductId = ref<number | null>(null)
const selectedAddressId = ref<number | null>(null)
const estimating = ref(false)
const estimatedPrice = ref<number | null>(null)
const submitting = ref(false)

const gradeForm = reactive({
  grade: 'B',
  appearanceDesc: '',
  functionDesc: '',
})

const selectedProduct = computed(() => {
  return products.value.find(p => p.id === selectedProductId.value)
})

const selectedAddress = computed(() => {
  return addresses.value.find(a => a.id === selectedAddressId.value)
})

onMounted(async () => {
  await Promise.all([
    loadProducts(),
    loadAddresses(),
  ])
  
  // 从URL参数获取产品ID
  if (route.query.productId) {
    selectedProductId.value = Number(route.query.productId)
  }
})

const loadProducts = async () => {
  try {
    const res = await productApi.getList()
    products.value = res.data.filter(p => p.status === 1)
  } catch (error) {
    console.error('Failed to load products:', error)
  }
}

const loadAddresses = async () => {
  try {
    const res = await addressApi.getList()
    addresses.value = res.data
    // 默认选择默认地址
    const defaultAddr = res.data.find(a => a.isDefault === 1)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    }
  } catch (error) {
    console.error('Failed to load addresses:', error)
  }
}

const handleProductChange = () => {
  estimatedPrice.value = null
}

const estimatePrice = async () => {
  if (!selectedProductId.value) return
  
  estimating.value = true
  try {
    const res = await recycleApi.estimate({
      productId: selectedProductId.value,
      grade: gradeForm.grade,
    })
    estimatedPrice.value = res.data
    ElMessage.success('估价成功')
  } catch (error) {
    console.error('Failed to estimate:', error)
  } finally {
    estimating.value = false
  }
}

const nextStep = () => {
  activeStep.value++
}

const prevStep = () => {
  activeStep.value--
}

const submitOrder = async () => {
  if (!selectedProductId.value || !selectedAddressId.value || estimatedPrice.value === null) {
    return
  }
  
  submitting.value = true
  try {
    await recycleApi.submit({
      productId: selectedProductId.value,
      addressId: selectedAddressId.value,
      grade: gradeForm.grade,
      appearanceDesc: gradeForm.appearanceDesc,
      functionDesc: gradeForm.functionDesc,
      estimatePrice: estimatedPrice.value,
    })
    
    ElMessage.success('订单提交成功')
    await router.push('/recycle/orders')
  } catch (error) {
    console.error('Failed to submit:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.recycle-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  border-radius: 12px;
  padding: 40px;
  color: #fff;
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.steps {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
}

.step-content {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  min-height: 400px;
}

.section-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.product-select {
  margin-bottom: 24px;
}

.selected-product {
  margin-top: 24px;
}

.selected-product h3 {
  font-size: 16px;
  margin-bottom: 16px;
  color: #303133;
}

.grade-form {
  max-width: 600px;
}

.estimated-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.estimated-price .price {
  font-size: 32px;
  font-weight: 600;
  color: #f56c6c;
}

.estimated-price .note {
  color: #909399;
  font-size: 12px;
}

.address-select {
  margin-bottom: 24px;
}

.address-item {
  display: flex;
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  width: 100%;
}

.address-item:hover {
  border-color: #409EFF;
}

.address-info .receiver {
  font-weight: 500;
  margin-bottom: 4px;
}

.address-info .detail {
  color: #909399;
  font-size: 14px;
}

.order-summary {
  margin-bottom: 24px;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 30px;
}

.step-actions el-button {
  min-width: 120px;
}
</style>