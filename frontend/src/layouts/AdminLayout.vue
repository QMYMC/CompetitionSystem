<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { filterMenus, layoutChildrenRoutes } from '@/router/routes'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { formatRoleList } from '@/utils/display'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const menus = computed(() => filterMenus(layoutChildrenRoutes, userStore.roles))
const roleSummary = computed(() => formatRoleList(userStore.roles))

async function handleCommand(command) {
  if (command === 'profile') {
    await router.push('/profile')
    return
  }

  if (command === 'logout') {
    await userStore.logout()
    ElMessage.success('已安全退出')
    await router.push('/login')
  }
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="248px" class="sidebar">
      <div class="brand-panel">
        <p class="brand-kicker">管理中心</p>
        <div class="brand">{{ appStore.systemName }}</div>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
        background-color="transparent"
        text-color="#465569"
        active-text-color="#2f5fa7"
      >
        <el-menu-item
          v-for="menu in menus"
          :key="menu.name"
          :index="`/${menu.path}`"
        >
          <el-icon><component :is="menu.meta.icon" /></el-icon>
          <span>{{ menu.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-main">
          <div>
            <p class="page-kicker">{{ roleSummary }}</p>
            <h1>{{ route.meta.title || '系统首页' }}</h1>
            <p class="page-description">
              {{ route.meta.description || '高校学科竞赛信息管理系统管理工作区。' }}
            </p>
          </div>

          <el-dropdown @command="handleCommand">
            <button type="button" class="user-card">
              <div>
                <strong>{{ userStore.displayName }}</strong>
                <span>{{ userStore.profile?.username }}</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </button>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="content">
        <div class="content-shell">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>
