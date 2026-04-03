<template>
  <div class="admin-dashboard">
    <!-- 图表区域 -->
    <div class="charts-container">
      <div class="chart-item">
        <h2>订单类型分布</h2>
        <div ref="orderTypeChart" class="chart"></div>
      </div>
      <div class="chart-item">
        <h2>数据概览</h2>
        <div ref="overviewChart" class="chart"></div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <div class="action-grid">
        <div class="action-item" @click="$router.push('/admin/users')">
          <el-icon>
            <User/>
          </el-icon>
          <span>用户管理</span>
        </div>
        <div class="action-item" @click="$router.push('/admin/recycles')">
          <el-icon>
            <DocumentCopy/>
          </el-icon>
          <span>回收订单</span>
        </div>
        <div class="action-item" @click="$router.push('/admin/exchanges')">
          <el-icon>
            <ShoppingCart/>
          </el-icon>
          <span>换新订单</span>
        </div>
        <div class="action-item" @click="$router.push('/admin/products')">
          <el-icon>
            <Goods/>
          </el-icon>
          <span>产品管理</span>
        </div>
        <div class="action-item" @click="$router.push('/admin/brands')">
          <el-icon>
            <Medal/>
          </el-icon>
          <span>品牌管理</span>
        </div>
        <div class="action-item" @click="$router.push('/admin/categories')">
          <el-icon>
            <Menu/>
          </el-icon>
          <span>分类管理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {reactive, onMounted, ref, watch} from 'vue'
import {User, DocumentCopy, ShoppingCart, Goods, Medal, Menu} from '@element-plus/icons-vue'
import {adminDashBoardApi} from "@/api/admin.ts"
import * as echarts from 'echarts'

const stats = reactive({
  userCount: 0,
  recycleCount: 0,
  exchangeCount: 0,
  productCount: 0,
})

const orderTypeChart = ref<HTMLElement | null>(null)
const overviewChart = ref<HTMLElement | null>(null)
let orderTypeChartInstance: echarts.ECharts | null = null
let overviewChartInstance: echarts.ECharts | null = null

const initCharts = () => {
  // 订单类型分布饼图
  if (orderTypeChart.value) {
    orderTypeChartInstance = echarts.init(orderTypeChart.value)
    const orderTypeOption = {
      title: {
        text: '订单类型分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: ['回收订单', '换新订单']
      },
      series: [
        {
          name: '订单类型',
          type: 'pie',
          radius: '50%',
          data: [
            {value: stats.recycleCount, name: '回收订单'},
            {value: stats.exchangeCount, name: '换新订单'}
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          color: ['#67C23A', '#E6A23C']
        }
      ]
    }
    orderTypeChartInstance.setOption(orderTypeOption)
  }

  // 数据概览柱状图
  if (overviewChart.value) {
    overviewChartInstance = echarts.init(overviewChart.value)
    const overviewOption = {
      title: {
        text: '数据概览',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['用户总数', '回收订单', '换新订单', '产品总数'],
        axisLabel: {
          interval: 0,
          rotate: 30
        }
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '数量',
          type: 'bar',
          data: [
            stats.userCount,
            stats.recycleCount,
            stats.exchangeCount,
            stats.productCount
          ],
          itemStyle: {
            color: function (params: any) {
              const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
              return colors[params.dataIndex]
            }
          }
        }
      ]
    }
    overviewChartInstance.setOption(overviewOption)
  }
}

const updateCharts = () => {
  if (orderTypeChartInstance) {
    orderTypeChartInstance.setOption({
      series: [
        {
          data: [
            {value: stats.recycleCount, name: '回收订单'},
            {value: stats.exchangeCount, name: '换新订单'}
          ]
        }
      ]
    })
  }

  if (overviewChartInstance) {
    overviewChartInstance.setOption({
      series: [
        {
          data: [
            stats.userCount,
            stats.recycleCount,
            stats.exchangeCount,
            stats.productCount
          ]
        }
      ]
    })
  }
}

onMounted(async () => {
  try {
    const response = await adminDashBoardApi.getStats()
    stats.userCount = response.data.userCount
    stats.recycleCount = response.data.recycleCount
    stats.exchangeCount = response.data.exchangeCount
    stats.productCount = response.data.productCount

    // 初始化图表
    setTimeout(initCharts, 100)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})

// 监听窗口大小变化，调整图表尺寸
window.addEventListener('resize', () => {
  orderTypeChartInstance?.resize()
  overviewChartInstance?.resize()
})

// 监听数据变化，更新图表
watch(
    () => [stats.userCount, stats.recycleCount, stats.exchangeCount, stats.productCount],
    () => {
      updateCharts()
    },
    {deep: true}
)
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.chart-item {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.chart-item h2 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 400px;
}

.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

.quick-actions h2 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #409EFF;
  color: #fff;
  transform: translateY(-3px);
}

.action-item el-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.action-item span {
  font-size: 14px;
}

@media (max-width: 1200px) {

  .charts-container {
    grid-template-columns: 1fr;
  }

  .action-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {

  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart {
    height: 300px;
  }
}
</style>