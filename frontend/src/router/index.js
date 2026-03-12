import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import DashboardView from '@/views/DashboardView.vue'
import LoginView from '@/views/LoginView.vue'
import ModulePlaceholderView from '@/views/ModulePlaceholderView.vue'
import NotFoundView from '@/views/NotFoundView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      title: '登录',
    },
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: DashboardView,
        meta: {
          title: '系统首页',
        },
      },
      {
        path: 'users',
        name: 'users',
        component: ModulePlaceholderView,
        meta: {
          title: '用户管理',
        },
      },
      {
        path: 'notices',
        name: 'notices',
        component: ModulePlaceholderView,
        meta: {
          title: '公告通知',
        },
      },
      {
        path: 'stats',
        name: 'stats',
        component: ModulePlaceholderView,
        meta: {
          title: '统计分析',
        },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach((to) => {
  const appTitle = import.meta.env.VITE_APP_TITLE || '高校学科竞赛信息管理系统'
  document.title = to.meta?.title ? `${to.meta.title} - ${appTitle}` : appTitle
})

export default router
