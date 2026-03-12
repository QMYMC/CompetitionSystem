import {
  Bell,
  DataAnalysis,
  Document,
  HomeFilled,
  Key,
  User,
  WarningFilled,
} from '@element-plus/icons-vue'
import CompetitionManagementView from '@/views/CompetitionManagementView.vue'
import DashboardView from '@/views/DashboardView.vue'
import LoginView from '@/views/LoginView.vue'
import ModulePlaceholderView from '@/views/ModulePlaceholderView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import UnauthorizedView from '@/views/UnauthorizedView.vue'
import UserManagementView from '@/views/UserManagementView.vue'

export const layoutChildrenRoutes = [
  {
    path: 'dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: {
      title: '系统首页',
      requiresAuth: true,
      menu: true,
      icon: HomeFilled,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: '系统概览与当前登录状态展示。',
    },
  },
  {
    path: 'users',
    name: 'users',
    component: UserManagementView,
    meta: {
      title: '用户管理',
      requiresAuth: true,
      menu: true,
      icon: User,
      roles: ['ADMIN'],
      description: '管理系统用户、角色归属和基础信息。',
    },
  },
  {
    path: 'competitions',
    name: 'competitions',
    component: CompetitionManagementView,
    meta: {
      title: '竞赛管理',
      requiresAuth: true,
      menu: true,
      icon: Document,
      roles: ['ADMIN'],
      description: '管理竞赛信息、报名时间、级别与状态。',
    },
  },
  {
    path: 'notices',
    name: 'notices',
    component: ModulePlaceholderView,
    meta: {
      title: '公告中心',
      requiresAuth: true,
      menu: true,
      icon: Bell,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: '阶段 6 将补充公告发布、列表与详情页面。',
    },
  },
  {
    path: 'stats',
    name: 'stats',
    component: ModulePlaceholderView,
    meta: {
      title: '统计分析',
      requiresAuth: true,
      menu: true,
      icon: DataAnalysis,
      roles: ['ADMIN', 'COLLEGE_AUDITOR'],
      description: '阶段 6 将补充图表展示与管理统计能力。',
    },
  },
  {
    path: 'profile',
    name: 'profile',
    component: ModulePlaceholderView,
    meta: {
      title: '个人信息',
      requiresAuth: true,
      menu: true,
      icon: Key,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: '当前页面用于展示登录用户资料与身份信息。',
    },
  },
]

export const baseRoutes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      title: '登录',
      guestOnly: true,
    },
  },
  {
    path: '/403',
    name: 'unauthorized',
    component: UnauthorizedView,
    meta: {
      title: '无权限访问',
      requiresAuth: true,
      icon: WarningFilled,
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: {
      title: '页面不存在',
    },
  },
]

export function hasPermission(route, roles = []) {
  const routeRoles = route.meta?.roles
  if (!routeRoles?.length) {
    return true
  }
  return routeRoles.some((role) => roles.includes(role))
}

export function filterMenus(routes, roles = []) {
  return routes.filter((route) => route.meta?.menu && hasPermission(route, roles))
}
