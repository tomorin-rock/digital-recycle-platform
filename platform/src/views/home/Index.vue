<template>
  <div class="home-page">
    <!-- Banner区域 -->
    <div class="banner">
      <div class="banner-content">
        <h1>数字产品回收平台</h1>
        <p>让闲置设备焕发新生，专业的回收换新服务</p>
        <div class="banner-actions">
          <el-button
            type="primary"
            size="large"
            @click="$router.push('/recycle')"
          >
            立即回收
          </el-button>
          <el-button size="large" @click="$router.push('/products')">
            浏览产品
          </el-button>
        </div>
      </div>
    </div>

    <!-- 服务特色 -->
    <div class="features">
      <div class="feature-item">
        <el-icon class="feature-icon"><Money /></el-icon>
        <h3>高价回收</h3>
        <p>专业评估，价格透明公正</p>
      </div>
      <div class="feature-item">
        <el-icon class="feature-icon"><Van /></el-icon>
        <h3>上门取件</h3>
        <p>全国覆盖，免费上门回收</p>
      </div>
      <div class="feature-item">
        <el-icon class="feature-icon"><RefreshRight /></el-icon>
        <h3>以旧换新</h3>
        <p>旧机抵扣，优惠换新机</p>
      </div>
      <div class="feature-item">
        <el-icon class="feature-icon"><Sunrise /></el-icon>
        <h3>安全可靠</h3>
        <p>数据清除，隐私保护</p>
      </div>
    </div>

    <!-- 热门产品 -->
    <div class="section">
      <div class="section-header">
        <h2>热门产品</h2>
        <el-button type="primary" text @click="$router.push('/products')">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="product-grid" v-loading="loading">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="$router.push(`/products/${product.id}`)"
        >
          <div class="product-image">
            <el-icon v-if="!product.imgUrl" class="placeholder-icon"
              ><Iphone
            /></el-icon>
            <img v-else :src="product.imgUrl" :alt="product.modelName" />
            <el-tag
              v-if="product.isNew"
              type="danger"
              effect="dark"
              class="new-tag"
              >新品</el-tag
            >
          </div>
          <div class="product-info">
            <p class="product-model">{{ product.modelName }}</p>
            <div class="product-price">
              <span class="price-label">市场价</span>
              <span class="price-value">¥{{ product.marketPrice }}</span>
            </div>
            <div class="recycle-price">
              <span class="recycle-label">回收价</span>
              <span class="recycle-value"
                >¥{{ product.recycleBasePrice }}起</span
              >
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 如何回收 -->
    <div class="section process-section">
      <h2>如何回收</h2>
      <div class="process-steps">
        <div class="step">
          <div class="step-number">1</div>
          <h4>选择设备</h4>
          <p>选择您的设备型号</p>
        </div>
        <el-icon class="step-arrow"><ArrowRight /></el-icon>
        <div class="step">
          <div class="step-number">2</div>
          <h4>在线估价</h4>
          <p>获取设备回收价格</p>
        </div>
        <el-icon class="step-arrow"><ArrowRight /></el-icon>
        <div class="step">
          <div class="step-number">3</div>
          <h4>预约回收</h4>
          <p>填写地址等待取件</p>
        </div>
        <el-icon class="step-arrow"><ArrowRight /></el-icon>
        <div class="step">
          <div class="step-number">4</div>
          <h4>收款到账</h4>
          <p>质检后快速打款</p>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <el-card
        shadow="hover"
        class="action-card"
        @click="$router.push('/recycle/orders')"
      >
        <template #header>
          <el-icon><DocumentCopy /></el-icon>
          <span>我的回收订单</span>
        </template>
        查看您的回收订单状态
      </el-card>

      <el-card
        shadow="hover"
        class="action-card"
        @click="$router.push('/exchange/orders')"
      >
        <template #header>
          <el-icon><ShoppingCart /></el-icon>
          <span>我的换新订单</span>
        </template>
        查看您的换新订单状态
      </el-card>

      <el-card
        shadow="hover"
        class="action-card"
        @click="$router.push('/coupons')"
      >
        <template #header>
          <el-icon><Ticket /></el-icon>
          <span>我的优惠券</span>
        </template>
        查看可用优惠券
      </el-card>

      <el-card
        shadow="hover"
        class="action-card"
        @click="$router.push('/forum')"
      >
        <template #header>
          <el-icon><ChatDotSquare /></el-icon>
          <span>社区论坛</span>
        </template>
        分享回收经验交流
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { productApi } from "@/api/product";
import type { Product } from "@/types";
import {
  Money,
  Van,
  RefreshRight,
  Sunrise,
  ArrowRight,
  Iphone,
  DocumentCopy,
  ShoppingCart,
  Ticket,
  ChatDotSquare,
} from "@element-plus/icons-vue";

const loading = ref(false);
const products = ref<Product[]>([]);

onMounted(async () => {
  loading.value = true;
  try {
    const res = await productApi.getList();
    products.value = res.data.slice(0, 8);
  } catch (error) {
    console.error("Failed to load products:", error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* Banner */
.banner {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border-radius: 16px;
  padding: 60px 40px;
  color: #fff;
  text-align: center;
  margin-bottom: 40px;
}

.banner h1 {
  font-size: 36px;
  margin-bottom: 16px;
}

.banner p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 30px;
}

.banner-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* Features */
.features {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.feature-item {
  background: #fff;
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
  transition: transform 0.3s;
}

.feature-item:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
}

.feature-item h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 8px;
}

.feature-item p {
  color: #909399;
  font-size: 14px;
}

/* Section */
.section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 24px;
  color: #303133;
}

/* Products */
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

.placeholder-icon {
  font-size: 64px;
  color: #c0c4cc;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.new-tag {
  position: absolute;
  top: 10px;
  left: 10px;
}

.product-info {
  padding: 16px;
}

.product-model {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.product-price,
.recycle-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.price-label,
.recycle-label {
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

/* Process */
.process-section h2 {
  text-align: center;
  margin-bottom: 40px;
}

.process-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
}

.step {
  text-align: center;
}

.step-number {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: #fff;
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.step h4 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 4px;
}

.step p {
  font-size: 12px;
  color: #909399;
}

.step-arrow {
  font-size: 24px;
  color: #c0c4cc;
}

/* Quick Actions */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.action-card {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-3px);
}

.action-card {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 600;
  color: #409eff;
}

.action-card {
  font-size: 20px;
}

/* Responsive */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .features {
    grid-template-columns: repeat(2, 1fr);
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .banner {
    padding: 40px 20px;
  }

  .banner h1 {
    font-size: 24px;
  }

  .process-steps {
    flex-wrap: wrap;
  }

  .step-arrow {
    display: none;
  }
}
</style>
