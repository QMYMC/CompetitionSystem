import {
  Bell,
  DataAnalysis,
  HomeFilled,
  Key,
  User,
  WarningFilled,
} from '@element-plus/icons-vue'
import DashboardView from '@/views/DashboardView.vue'
import LoginView from '@/views/LoginView.vue'
import ModulePlaceholderView from '@/views/ModulePlaceholderView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import UnauthorizedView from '@/views/UnauthorizedView.vue'

export const layoutChildrenRoutes = [
  {
    path: 'dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: {
      title: 'Dashboard',
      requiresAuth: true,
      menu: true,
      icon: HomeFilled,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: 'System overview and quick stats.',
    },
  },
  {
    path: 'users',
    name: 'users',
    component: ModulePlaceholderView,
    meta: {
      title: 'User Management',
      requiresAuth: true,
      menu: true,
      icon: User,
      roles: ['ADMIN'],
      description: 'Phase 4 will add user CRUD and role assignment.',
    },
  },
  {
    path: 'notices',
    name: 'notices',
    component: ModulePlaceholderView,
    meta: {
      title: 'Notice Center',
      requiresAuth: true,
      menu: true,
      icon: Bell,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: 'Phase 6 will add notice publish and detail pages.',
    },
  },
  {
    path: 'stats',
    name: 'stats',
    component: ModulePlaceholderView,
    meta: {
      title: 'Statistics',
      requiresAuth: true,
      menu: true,
      icon: DataAnalysis,
      roles: ['ADMIN', 'COLLEGE_AUDITOR'],
      description: 'Phase 6 will add charts and management statistics.',
    },
  },
  {
    path: 'profile',
    name: 'profile',
    component: ModulePlaceholderView,
    meta: {
      title: 'Profile',
      requiresAuth: true,
      menu: true,
      icon: Key,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: 'Current page displays user profile and login context.',
    },
  },
]

export const baseRoutes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      title: 'Login',
      guestOnly: true,
    },
  },
  {
    path: '/403',
    name: 'unauthorized',
    component: UnauthorizedView,
    meta: {
      title: 'Forbidden',
      requiresAuth: true,
      icon: WarningFilled,
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: {
      title: 'Not Found',
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
