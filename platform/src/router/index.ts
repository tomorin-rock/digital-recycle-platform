import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  // 公开页面
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', guest: true },
  },
  
  // 用户页面 - 需要登录
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/Index.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/product/List.vue'),
        meta: { title: '产品列表' },
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        component: () => import('@/views/product/Detail.vue'),
        meta: { title: '产品详情' },
      },
      {
        path: 'recycle',
        name: 'Recycle',
        component: () => import('@/views/recycle/Index.vue'),
        meta: { title: '设备回收' },
      },
      {
        path: 'recycle/orders',
        name: 'RecycleOrders',
        component: () => import('@/views/recycle/Orders.vue'),
        meta: { title: '回收订单' },
      },
      {
        path: 'exchange',
        name: 'Exchange',
        component: () => import('@/views/exchange/Index.vue'),
        meta: { title: '以旧换新' },
      },
      {
        path: 'exchange/orders',
        name: 'ExchangeOrders',
        component: () => import('@/views/exchange/Orders.vue'),
        meta: { title: '换新订单' },
      },
      {
        path: 'addresses',
        name: 'Addresses',
        component: () => import('@/views/address/Index.vue'),
        meta: { title: '地址管理' },
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/forum/Index.vue'),
        meta: { title: '社区论坛' },
      },
      {
        path: 'forum/:id',
        name: 'PostDetail',
        component: () => import('@/views/forum/Detail.vue'),
        meta: { title: '帖子详情' },
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('@/views/user/Index.vue'),
        meta: { title: '个人中心' },
      },
      {
        path: 'coupons',
        name: 'Coupons',
        component: () => import('@/views/user/Coupons.vue'),
        meta: { title: '我的优惠券' },
      },
    ],
  },
  
  // 管理员页面
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台' },
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue'),
        meta: { title: '产品管理' },
      },
      {
        path: 'brands',
        name: 'AdminBrands',
        component: () => import('@/views/admin/Brands.vue'),
        meta: { title: '品牌管理' },
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue'),
        meta: { title: '分类管理' },
      },
      {
        path: 'recycles',
        name: 'AdminRecycles',
        component: () => import('@/views/admin/Recycles.vue'),
        meta: { title: '回收订单管理' },
      },
      {
        path: 'exchanges',
        name: 'AdminExchanges',
        component: () => import('@/views/admin/Exchanges.vue'),
        meta: { title: '换新订单管理' },
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/Posts.vue'),
        meta: { title: '帖子管理' },
      },
      {
        path: 'logs',
        name: 'AdminLogs',
        component: () => import('@/views/admin/Logs.vue'),
        meta: { title: '操作日志' },
      },
    ],
  },
  
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '页面不存在' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || '数字产品回收平台'} - 数字产品回收平台`
  
  const userStore = useUserStore()
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      return next({ name: 'Login', query: { redirect: to.fullPath } })
    }
    
    // 获取用户信息
    if (!userStore.userInfo) {
      await userStore.fetchUserInfo()
    }
    
    // 需要管理员权限
    if (to.meta.requiresAdmin && !userStore.isAdmin) {
      return next({ name: 'Home' })
    }
  }
  
  // 游客页面（已登录用户不能访问）
  if (to.meta.guest && userStore.isLoggedIn) {
    return next({ name: 'Home' })
  }
  
  next()
})

export default router
