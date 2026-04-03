<template>
  <div class="exchange-page">
    <div class="page-header">
      <h1>以旧换新</h1>
      <p>用旧设备抵扣，优惠换新机</p>
    </div>

    <el-steps :active="activeStep" finish-status="success" align-center class="steps">
      <el-step title="选择旧机" />
      <el-step title="选择新机" />
      <el-step title="使用优惠券" />
      <el-step title="确认订单" />
    </el-steps>

    <!-- 步骤1: 选择旧机 -->
    <div v-show="activeStep === 0" class="step-content">
      <div class="section-title">选择可用的回收订单</div>

      <div class="recycle-list" v-loading="loadingRecycles">
        <el-empty v-if="recycleOrders.length === 0" description="暂无可用的回收订单">
          <el-button type="primary" @click="$router.push('/recycle')">去回收</el-button>
        </el-empty>

        <div v-else class="order-cards">
          <div
            v-for="order in recycleOrders"
            :key="order.id"
            class="order-card"
            :class="{ active: selectedRecycleId === order.id }"
            @click="selectRecycle(order)"
          >
            <div class="order-header">
              <span class="order-no">{{ order.orderNo }}</span>
              <el-tag :type="getStatusType(order.status)" size="small">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
            <div class="order-body">
              <div class="product-name">{{ order.productSnapshot }}</div>
              <div class="price">
                <span class="label">回收价:</span>
                <span class="value">¥{{ order.finalPrice || order.estimatePrice }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="step-actions">
        <el-button type="primary" size="large" :disabled="!selectedRecycleId" @click="nextStep">
          下一步
        </el-button>
      </div>
    </div>

    <!-- 步骤2: 选择新机 -->
    <div v-show="activeStep === 1" class="step-content">
      <div class="section-title">选择新产品</div>

      <div class="search-box">
        <el-input 
          placeholder="请输入产品名称" 
          v-model="searchQuery" 
          clearable
          @keyup.enter="loadProducts"
        >
          <template #prefix>
            <el-icon>
              <Search/>
            </el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadProducts">搜索</el-button>
      </div>

      <div class="product-list" v-loading="loadingProducts">
        <div class="product-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            :class="{ active: selectedProductId === product.id }"
            @click="selectProduct(product)"
          >
            <div class="product-image" @click="nextStep">
              <el-icon v-if="!product.imgUrl" class="placeholder-icon">
                <Iphone />
              </el-icon>
              <img v-else :src="product.imgUrl" alt="产品图片" style="max-width: 100%; max-height: 100%; object-fit: contain"/>
            </div>
            <div class="product-info">
              <div class="product-title">{{ product.modelName }}</div>
              <div class="product-price">¥{{ product.marketPrice }}</div>
              <div class="product-stock">库存: {{ product.stock }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="step-actions">
        <el-button size="large" @click="prevStep">上一步</el-button>
        <el-button type="primary" size="large" :disabled="!selectedProductId" @click="nextStep">
          下一步
        </el-button>
      </div>
    </div>

    <!-- 步骤3: 使用优惠券 -->
    <div v-show="activeStep === 2" class="step-content">
      <div class="section-title">选择优惠券（可选）</div>

      <div class="coupon-list" v-loading="loadingCoupons">
        <el-empty v-if="coupons.length === 0" description="暂无可用优惠券" />

        <div v-else class="coupon-cards">
          <div
            v-for="coupon in coupons"
            :key="coupon.id"
            class="coupon-card"
            :class="{ active: selectedCouponId === coupon.id }"
            @click="selectCoupon(coupon)"
          >
            <div class="coupon-amount">
              <span class="currency">¥</span>
              <span class="value">{{ coupon.amount }}</span>
            </div>
            <div class="coupon-info">
              <div class="coupon-code">{{ coupon.code }}</div>
              <div class="coupon-expire">有效期至 {{ formatDate(coupon.expireTime) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="step-actions">
        <el-button size="large" @click="prevStep">上一步</el-button>
        <el-button type="primary" size="large" @click="nextStep">
          {{ selectedCouponId ? '使用优惠券' : '不使用优惠券' }}
        </el-button>
      </div>
    </div>

    <!-- 步骤4: 确认订单 -->
    <div v-show="activeStep === 3" class="step-content">
      <div class="section-title">确认订单</div>

      <div class="order-summary">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="旧机订单">{{ selectedRecycle?.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="旧机抵扣">¥{{ recycleDeduction }}</el-descriptions-item>
          <el-descriptions-item label="新产品">{{ selectedProduct?.modelName }}</el-descriptions-item>
          <el-descriptions-item label="产品价格">¥{{ selectedProduct?.marketPrice }}</el-descriptions-item>
          <el-descriptions-item label="优惠券" v-if="selectedCoupon">
            -¥{{ selectedCoupon.amount }}
          </el-descriptions-item>
          <el-descriptions-item label="应付金额">
            <span class="pay-amount">¥{{ totalAmount }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="address-section" v-if="defaultAddress">
        <h3>收货地址</h3>
        <div class="address-info">
          {{ defaultAddress.province }}{{ defaultAddress.city }}{{ defaultAddress.district }}{{ defaultAddress.detail }}
          ({{ defaultAddress.receiverName }} {{ defaultAddress.receiverPhone }})
        </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { recycleApi, exchangeApi } from '@/api/order'
import { productApi } from '@/api/product'
import { addressApi, couponApi } from '@/api/user'
import type { RecycleOrder, Product, Coupon, Address } from '@/types'
import { ElMessage } from 'element-plus'
import { Iphone, Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 状态管理
const activeStep = ref(0)
const loadingRecycles = ref(false)
const loadingProducts = ref(false)
const loadingCoupons = ref(false)
const submitting = ref(false)
const searchQuery = ref('')

// 数据管理
const recycleOrders = ref<RecycleOrder[]>([])
const products = ref<Product[]>([])
const coupons = ref<Coupon[]>([])
const defaultAddress = ref<Address | null>(null)

// 选中状态
const selectedRecycleId = ref<number | null>(null)
const selectedProductId = ref<number | null>(null)
const selectedCouponId = ref<number | null>(null)


// 计算属性
const selectedRecycle = computed(() => {
  return recycleOrders.value.find(o => o.id === selectedRecycleId.value)
})

const selectedProduct = computed(() => {
  return products.value.find(p => p.id === selectedProductId.value)
})

const selectedCoupon = computed(() => {
  return coupons.value.find(c => c.id === selectedCouponId.value)
})

const recycleDeduction = computed(() => {
  return selectedRecycle.value?.finalPrice || selectedRecycle.value?.estimatePrice || 0
})

const totalAmount = computed(() => {
  let total = selectedProduct.value?.marketPrice || 0
  total -= recycleDeduction.value
  if (selectedCoupon.value) {
    total -= selectedCoupon.value.amount
  }
  return Math.max(0, total)
})

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadRecycleOrders(),
    loadProducts(),
    loadCoupons(),
    loadDefaultAddress()
  ])

  // 从URL参数获取回收订单ID
  if (route.query.recycleId) {
    selectedRecycleId.value = Number(route.query.recycleId)
    activeStep.value = 1
  }
  if (route.query.newProductId) {
    selectedProductId.value = Number(route.query.newProductId)
  }
})

// 数据加载
const loadRecycleOrders = async () => {
  loadingRecycles.value = true
  try {
    const res = await recycleApi.getMyList()
    // 只显示可以换新的订单（已提交、质检中、已质检）
    recycleOrders.value = res.data.filter(o => 
      ['SUBMITTED', 'INSPECTING', 'INSPECTED'].includes(o.status)
    )
  } catch (error) {
    console.error('Failed to load recycles:', error)
  } finally {
    loadingRecycles.value = false
  }
}

const loadProducts = async () => {
  loadingProducts.value = true
  try {
    const res = await productApi.getList(searchQuery.value || undefined)
    products.value = res.data.filter(p => p.status === 1 && p.stock > 0)
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loadingProducts.value = false
  }
}

const loadCoupons = async () => {
  loadingCoupons.value = true
  try {
    const res = await couponApi.getList()
    // 只显示未使用的优惠券
    const now = new Date()
    coupons.value = res.data.filter(c => 
      c.status === 'UNUSED' && new Date(c.expireTime) > now
    )
  } catch (error) {
    console.error('Failed to load coupons:', error)
  } finally {
    loadingCoupons.value = false
  }
}

const loadDefaultAddress = async () => {
  try {
    const res = await addressApi.getDefault()
    defaultAddress.value = res.data
  } catch (error) {
    console.error('Failed to load address:', error)
  }
}

// 辅助函数
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    SUBMITTED: 'info',
    INSPECTING: 'warning',
    INSPECTED: 'success',
    FINISHED: 'success',
    CANCELLED: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    SUBMITTED: '已提交',
    INSPECTING: '质检中',
    INSPECTED: '已质检',
    FINISHED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 事件处理
const selectRecycle = (order: RecycleOrder) => {
  selectedRecycleId.value = order.id
}

const selectProduct = (product: Product) => {
  selectedProductId.value = product.id
}

const selectCoupon = (coupon: Coupon) => {
  if (selectedCouponId.value === coupon.id) {
    selectedCouponId.value = null
  } else {
    selectedCouponId.value = coupon.id
  }
}

const nextStep = () => {
  activeStep.value++
}

const prevStep = () => {
  activeStep.value--
}

const submitOrder = async () => {
  if (!selectedRecycleId.value || !selectedProductId.value || !defaultAddress.value) {
    ElMessage.warning('请完善订单信息')
    return
  }

  submitting.value = true
  try {
    const data: any = {
      recycleId: selectedRecycleId.value,
      newProductId: selectedProductId.value,
      addressId: defaultAddress.value.id
    }

    if (selectedCouponId.value) {
      data.couponId = selectedCouponId.value
    }

    await exchangeApi.create(data)
    ElMessage.success('订单创建成功')
    await router.push('/exchange/orders')
  } catch (error) {
    console.error('Failed to submit:', error)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.exchange-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  background: linear-gradient(135deg, #67C23A 0%, #409EFF 100%);
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
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.step-content {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  min-height: 400px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

/* 回收订单列表 */
.order-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.order-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.order-card.active {
  border-color: #409EFF;
  background: #f0f7ff;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 500;
  color: #303133;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.price .label {
  color: #909399;
  font-size: 12px;
}

.price .value {
  color: #f56c6c;
  font-weight: 600;
}

.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-box el-input {
  width: 300px;
}

/* 产品列表 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.product-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.product-card:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.product-card.active {
  border-color: #67C23A;
  background: #f0f9eb;
}

.product-image {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.placeholder-icon {
  font-size: 48px;
  color: #c0c4cc;
}

.product-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.product-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: 600;
}

.product-stock {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 优惠券列表 */
.coupon-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.coupon-card {
  display: flex;
  align-items: center;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.coupon-card:hover {
  border-color: #E6A23C;
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.15);
}

.coupon-card.active {
  border-color: #E6A23C;
  background: #fdf6ec;
}

.coupon-amount {
  background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
  color: #fff;
  padding: 20px;
  min-width: 80px;
  text-align: center;
}

.coupon-amount .currency {
  font-size: 14px;
}

.coupon-amount .value {
  font-size: 28px;
  font-weight: 600;
}

.coupon-info {
  padding: 12px 16px;
  flex: 1;
}

.coupon-code {
  font-weight: 500;
  margin-bottom: 4px;
  color: #303133;
}

.coupon-expire {
  font-size: 12px;
  color: #909399;
}

/* 订单摘要 */
.order-summary {
  margin-bottom: 24px;
}

.pay-amount {
  font-size: 24px;
  color: #f56c6c;
  font-weight: 600;
}

/* 地址信息 */
.address-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.address-section h3 {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.address-info {
  color: #303133;
  line-height: 1.4;
}

/* 步骤按钮 */
.step-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.step-actions el-button {
  min-width: 120px;
  padding: 10px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exchange-page {
    padding: 10px;
  }

  .page-header {
    padding: 30px 20px;
  }

  .steps,
  .step-content {
    padding: 20px;
  }

  .order-cards,
  .coupon-cards {
    grid-template-columns: 1fr;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .step-actions {
    flex-direction: column;
    align-items: center;
  }

  .step-actions el-button {
    width: 100%;
    max-width: 200px;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .section-title {
    font-size: 16px;
  }
}
</style>