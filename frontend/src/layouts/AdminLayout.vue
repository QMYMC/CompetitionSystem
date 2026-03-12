<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Bell, DataAnalysis, HomeFilled, User } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const appStore = useAppStore()

const activeMenu = computed(() => route.path)

const menus = [
  { index: '/dashboard', title: '系统首页', icon: HomeFilled },
  { index: '/users', title: '用户管理', icon: User },
  { index: '/notices', title: '公告通知', icon: Bell },
  { index: '/stats', title: '统计分析', icon: DataAnalysis },
]
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="sidebar">
      <div class="brand">{{ appStore.systemName }}</div>
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
          :key="menu.index"
          :index="menu.index"
        >
          <el-icon><component :is="menu.icon" /></el-icon>
          <span>{{ menu.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div>
          <h1>管理端工作台</h1>
          <p>阶段 1 仅初始化项目骨架，后续业务模块按任务书逐步补齐。</p>
        </div>
        <el-button type="primary" plain @click="$router.push('/login')">查看登录页占位</el-button>
      </el-header>

      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
