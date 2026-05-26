import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'dashboard' }
      }
    ]
  },
  {
    path: '/charging',
    component: Layout,
    redirect: '/charging/list',
  meta: { title: '充电桩管理', icon: 'charging' },
    children: [
      {
        path: 'list',
        name: 'ChargingList',
        component: () => import('@/views/charging/list.vue'),
        meta: { title: '充电桩列表' }
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    redirect: '/order/list',
  meta: { title: '订单管理', icon: 'order' },
    children: [
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { title: '订单列表' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
  meta: { title: '用户管理', icon: 'user' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户列表' }
      }
    ]
  },
  {
    path: '/reservation',
    component: Layout,
    redirect: '/reservation/list',
  meta: { title: '预约管理', icon: 'reservation' },
    children: [
      {
        path: 'list',
        name: 'ReservationList',
        component: () => import('@/views/reservation/list.vue'),
        meta: { title: '预约列表' }
      }
    ]
  },
  {
    path: '/refund',
    component: Layout,
    redirect: '/refund/list',
  meta: { title: '退款管理', icon: 'refund' },
    children: [
      {
        path: 'list',
        name: 'RefundList',
        component: () => import('@/views/refund/list.vue'),
        meta: { title: '退款审核' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
