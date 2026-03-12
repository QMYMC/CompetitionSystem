import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'
import pinia from '@/stores'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { baseRoutes, hasPermission, layoutChildrenRoutes } from '@/router/routes'

const routes = [
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    meta: {
      requiresAuth: true,
    },
    children: layoutChildrenRoutes,
  },
  ...baseRoutes,
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const userStore = useUserStore(pinia)

  if (!userStore.initialized) {
    try {
      await userStore.bootstrap()
    } catch {
      return {
        path: '/login',
        query: to.path !== '/login' ? { redirect: to.fullPath } : {},
      }
    }
  }

  const requiresAuth = to.matched.some((record) => record.meta?.requiresAuth)

  if (to.meta?.guestOnly && userStore.isAuthenticated) {
    return to.query.redirect || '/dashboard'
  }

  if (requiresAuth && !userStore.hasToken) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (requiresAuth && !hasPermission(to, userStore.roles)) {
    return '/403'
  }

  return true
})

router.afterEach((to) => {
  const appStore = useAppStore(pinia)
  document.title = to.meta?.title
    ? `${to.meta.title} | ${appStore.systemName}`
    : appStore.systemName
})

export default router
