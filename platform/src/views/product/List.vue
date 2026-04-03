<template>
  <div class="list-container">
    <div class="product-list-page">
      <div class="page-header">
        <h1>产品列表</h1>
        <p>选择您要回收或换新的设备</p>
      </div>

      <!-- 品牌筛选 -->
      <div class="brand-filter">
        <div class="brand-list" v-loading="brandsLoading">
          <div 
            v-for="brand in brands" 
            :key="brand.id"
            class="brand-item"
            @click="handleBrandSearch(brand.id)"
          >
            <el-image 
              :src="brand.logo"
              :alt="brand.name"
              class="brand-logo"
            />
            <span class="brand-name">{{ brand.name }}</span>
          </div>
        </div>
      </div>

      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <!-- 分类筛选 -->
        <el-cascader
            v-model="categoryCascaderValue"
            :options="categoryOptions"
            :props="cascaderProps"
            placeholder="按分类筛选"
            clearable
            @change="handleCategoryChange"
            style="width: 200px"
        />
        <!-- 根据型号搜索：-->
        <el-input
            v-model="searchKeyword"
            placeholder="搜索产品型号"
            clearable
            @keyup.enter="handleSearch"
            style="width: 300px"
        >
          <template #prefix>
            <el-icon>
              <Search/>
            </el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <!-- 产品列表 -->
      <div class="product-list" v-loading="loading">
        <el-empty v-if="products.length === 0 && !loading" description="暂无产品"/>

        <div class="product-grid" v-else>
          <div
              v-for="product in products"
              :key="product.id"
              class="product-card"
              @click="$router.push(`/products/${product.id}`)"
          >
            <div class="product-image">
              <el-image :src="product.imgUrl" :alt="product.modelName" class="product-image"></el-image>
              <el-tag v-if="product.isNew" type="danger" effect="dark" class="new-tag">新品</el-tag>
              <el-tag v-if="product.status === 0" type="info" effect="dark" class="status-tag">已下架</el-tag>
            </div>
            <div class="product-info">
              <p class="product-model">{{ product.modelName }}</p>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-price">
                <span class="price-label">市场价</span>
                <span class="price-value">¥{{ product.marketPrice }}</span>
              </div>
              <div class="recycle-price">
                <span class="recycle-label">回收价</span>
                <span class="recycle-value">¥{{ product.recycleBasePrice }}起</span>
              </div>
              <div class="product-stock">
                库存: {{ product.stock }}件
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 回到顶部按钮 -->
    <el-backtop :right="40" :bottom="40" :visibility-height="300">
      <template #default>
        <div class="backtop-button">
          <el-icon class="backtop-icon"><ArrowUp /></el-icon>
        </div>
      </template>
    </el-backtop>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, computed} from 'vue'
import {productApi} from '@/api/product'
import type {Product, ProductBrand, ProductCategory} from '@/types'
import {Search, ArrowUp} from '@element-plus/icons-vue'

const loading = ref(false)
const brandsLoading = ref(false)
const categoriesLoading = ref(false)
const products = ref<Product[]>([])
const brands = ref<ProductBrand[]>([])
const categories = ref<ProductCategory[]>([])
const searchKeyword = ref('')
const currentBrandId = ref<number | null>(null)
const currentParentId = ref<number | null>(null)
const currentCategoryId = ref<number | null>(null)
const categoryCascaderValue = ref<number[]>([])

// 级联选择器配置
const cascaderProps = {
  checkStrictly: true,
  emitPath: true
}

// 构建级联选择器的选项
const categoryOptions = computed(() => {
  const options: any[] = []
  
  // 先找出所有父分类
  const parentCategories = categories.value.filter(cat => !cat.parentId || cat.parentId === 0)
  
  // 为每个父分类添加子分类
  parentCategories.forEach(parent => {
    const childCats = categories.value
      .filter(cat => cat.parentId === parent.id)
      .map(child => ({
        value: child.id,
        label: child.name
      }))
    
    options.push({
      value: parent.id,
      label: parent.name,
      children: childCats.length > 0 ? childCats : undefined
    })
  })
  
  return options
})

onMounted(async () => {
  await Promise.all([
    loadProducts(),
    loadBrands(),
    loadCategories()
  ])
})

const loadBrands = async () => {
  brandsLoading.value = true
  try {
    const res = await productApi.getBrands()
    brands.value = res.data
  } catch (error) {
    console.error('Failed to load brands:', error)
  } finally {
    brandsLoading.value = false
  }
}

const loadCategories = async () => {
  categoriesLoading.value = true
  try {
    const res = await productApi.getCategories()
    categories.value = res.data
  } catch (error) {
    console.error('Failed to load categories:', error)
  } finally {
    categoriesLoading.value = false
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    let res
    if (currentBrandId.value) {
      // 按品牌查询
      res = await productApi.getByBrand(currentBrandId.value)
      products.value = res.data
    } else if (currentParentId.value) {
      // 按分类查询
      res = await productApi.getByCategory(currentParentId.value, currentCategoryId.value || undefined)
      products.value = res.data.products
    } else {
      // 按关键词查询
      res = await productApi.getList(searchKeyword.value || undefined)
      products.value = res.data
    }
  } catch (error) {
    console.error('Failed to load products:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentBrandId.value = null
  currentParentId.value = null
  currentCategoryId.value = null
  categoryCascaderValue.value = []
  loadProducts()
}

const handleBrandSearch = (brandId: number) => {
  currentBrandId.value = brandId
  currentParentId.value = null
  currentCategoryId.value = null
  categoryCascaderValue.value = []
  searchKeyword.value = ''
  loadProducts()
}

const handleCategoryChange = (value: number[]) => {
  if (!value || value.length === 0) {
    currentParentId.value = null
    currentCategoryId.value = null
    currentBrandId.value = null
    searchKeyword.value = ''
  } else if (value.length === 1) {
    // 只选择了父分类
    currentParentId.value = value[0]
    currentCategoryId.value = null
    currentBrandId.value = null
    searchKeyword.value = ''
  } else if (value.length === 2) {
    // 选择了父分类和子分类
    currentParentId.value = value[0]
    currentCategoryId.value = value[1]
    currentBrandId.value = null
    searchKeyword.value = ''
  }
  loadProducts()
}

</script>

<style scoped>
.product-list-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  border-radius: 12px;
  padding: 40px;
  color: #fff;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.page-header p {
  opacity: 0.9;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
  }
  
  .filter-bar el-cascader,
  .filter-bar el-input {
    width: 100% !important;
  }
}

.product-list {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  min-height: 400px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #f5f7fa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  position: relative;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.new-tag, .status-tag {
  position: absolute;
  top: 10px;
  left: 10px;
}

.status-tag {
  left: auto;
  right: 10px;
}

.product-info {
  padding: 16px;
}

.product-model {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.product-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price, .recycle-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.price-label, .recycle-label {
  font-size: 12px;
  color: #909399;
}

.price-value {
  font-size: 14px;
  color: #606266;
  text-decoration: line-through;
}

.recycle-value {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 600;
}

.product-stock {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.brand-filter {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.brand-filter h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

.brand-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.brand-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  padding: 12px;
  border-radius: 8px;
  width: 80px;
}

.brand-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}

.brand-logo {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  margin-bottom: 8px;
  object-fit: contain;
}

.brand-name {
  font-size: 12px;
  color: #606266;
  text-align: center;
  line-height: 1.2;
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
  
  .brand-list {
    gap: 12px;
  }
  
  .brand-item {
    width: 70px;
  }
  
  .brand-logo {
    width: 40px;
    height: 40px;
  }
}

.backtop-button {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.backtop-button:hover {
  background: #66B1FF;
  transform: translateY(-2px);
}

</style>