<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { filterMenus, layoutChildrenRoutes } from '@/router/routes'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const menus = computed(() => filterMenus(layoutChildrenRoutes, userStore.roles))
const roleSummary = computed(() => userStore.roles.join(' / ') || '暂无角色')

async function handleCommand(command) {
  if (command === 'profile') {
    await router.push('/profile')
    return
  }

  if (command === 'logout') {
    await userStore.logout()
    ElMessage.success('已退出登录。')
    await router.push('/login')
  }
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="248px" class="sidebar">
      <div class="brand-panel">
        <p class="brand-kicker">后台管理</p>
        <div class="brand">{{ appStore.systemName }}</div>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
        background-color="transparent"
        text-color="#dbe7e4"
        active-text-color="#ffffff"
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
        <div>
          <p class="page-kicker">{{ roleSummary }}</p>
          <h1>{{ route.meta.title || '系统首页' }}</h1>
          <p class="page-description">
            {{ route.meta.description || '高校学科竞赛信息管理系统后台工作区。' }}
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
      </el-header>

      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
