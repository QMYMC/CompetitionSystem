<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const cards = computed(() => [
  {
    label: '当前登录账号',
    value: userStore.profile?.username || '--',
    tip: '通过阶段 2 的登录接口完成身份认证。',
  },
  {
    label: '当前角色',
    value: userStore.roles.join(', ') || '--',
    tip: '左侧菜单会根据路由权限进行动态过滤。',
  },
  {
    label: '受保护路由',
    value: '已启用',
    tip: '未登录访问受保护页面时会自动跳转到登录页。',
  },
  {
    label: '后端联调状态',
    value: '已连接',
    tip: 'Axios 请求封装与 Token 拦截器已生效。',
  },
])

const nextSteps = [
  '阶段 4 将实现用户管理相关业务功能。',
  '阶段 5 将补充报名、组队与审核流程。',
  '阶段 6 将补充获奖、公告与统计图表。',
]
</script>

<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div>
        <p class="hero-kicker">后台首页</p>
        <h2>系统首页</h2>
        <p class="hero-text">
          当前已进入受保护的后台工作区。此页面用于确认登录闭环、路由守卫、Pinia 用户状态、
          Axios 请求封装以及后端认证联调均已正常工作。
        </p>
      </div>

      <div class="status-stack">
        <el-tag size="large" type="success">已登录</el-tag>
        <el-tag size="large" type="info">{{ userStore.displayName }}</el-tag>
      </div>
    </section>

    <section class="metric-grid">
      <el-card v-for="card in cards" :key="card.label" shadow="hover" class="metric-card">
        <p class="metric-label">{{ card.label }}</p>
        <h3 class="metric-value metric-value--compact">{{ card.value }}</h3>
        <p class="metric-tip">{{ card.tip }}</p>
      </el-card>
    </section>

    <section class="module-panel">
      <div class="section-header">
        <h3>当前登录信息</h3>
        <p>用户资料来自 `GET /api/auth/info` 接口，并在 Token 校验通过后加载。</p>
      </div>

      <div class="profile-grid">
        <div class="profile-item">
          <span>姓名</span>
          <strong>{{ userStore.profile?.realName || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>用户名</span>
          <strong>{{ userStore.profile?.username || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>用户类型</span>
          <strong>{{ userStore.profile?.userType || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>所属学院</span>
          <strong>{{ userStore.profile?.collegeName || '--' }}</strong>
        </div>
      </div>
    </section>

    <section class="module-panel">
      <div class="section-header">
        <h3>后续阶段预告</h3>
        <p>当前只实现了阶段 3 的前端基础框架，业务模块仍保留占位状态。</p>
      </div>

      <div class="module-grid">
        <div v-for="item in nextSteps" :key="item" class="module-item">
          {{ item }}
        </div>
      </div>
    </section>
  </div>
</template>
