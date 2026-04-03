<template>
  <el-container class="main-layout">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon><Refresh /></el-icon>
          <span>数字产品回收平台</span>
        </router-link>
      </div>
      
      <el-menu
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
        router
        :default-active="$route.path"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/products">产品</el-menu-item>
        <el-menu-item index="/recycle">回收</el-menu-item>
        <el-menu-item index="/exchange">换新</el-menu-item>
        <el-menu-item index="/forum">社区</el-menu-item>
      </el-menu>
      
      <div class="header-right">
        <el-dropdown v-if="userStore.isLoggedIn" trigger="click">
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <span class="nickname">{{ userStore.userInfo?.nickname }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/user')">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/addresses')">
                <el-icon><Location /></el-icon>地址管理
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/coupons')">
                <el-icon><Ticket /></el-icon>我的优惠券
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <template v-else>
          <el-button type="primary" text @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
        
        <el-button
          v-if="userStore.isAdmin"
          type="warning"
          @click="$router.push('/admin')"
        >
          <el-icon><Setting /></el-icon>
          管理后台
        </el-button>
      </div>
    </el-header>
    
    <!-- 主内容 -->
    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
    
    <!-- 底部 -->
    <el-footer class="footer">
      <p>© 数字产品回收平台 - 让闲置设备焕发新生</p>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { Refresh, ArrowDown, User, Location, Ticket, SwitchButton, Setting } from '@element-plus/icons-vue'

const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 0 24px;
  height: 60px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.header-left .logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
  text-decoration: none;
}

.header-left .logo el-icon {
  font-size: 24px;
}

.nav-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.nickname {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main-content {
  flex: 1;
  margin-top: 60px;
  padding: 20px;
  background: #f5f7fa;
}

.footer {
  text-align: center;
  color: #909399;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 20px;
}
</style>