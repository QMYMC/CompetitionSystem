<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const cards = computed(() => [
  {
    label: '当前登录账号',
    value: userStore.profile?.username || '--',
    tip: '认证信息来自阶段 2 的登录接口。',
  },
  {
    label: '当前角色',
    value: userStore.roles.join(', ') || '--',
    tip: '左侧菜单会根据角色动态过滤。',
  },
  {
    label: '受保护路由',
    value: '已启用',
    tip: '未登录访问后台页面时会自动跳转到登录页。',
  },
  {
    label: '阶段 6 进度',
    value: '已完成',
    tip: '获奖、公告和统计分析模块已可联调演示。',
  },
])

const nextSteps = [
  '学生可演示个人赛报名、团队创建、成员维护、获奖填报与查看审核结果。',
  '管理员可演示报名审核、获奖审核、公告发布编辑删除，以及统计图表展示。',
  '系统状态流转已覆盖报名和获奖的待审核、通过、驳回，适合毕业设计答辩展示。',
]
</script>

<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div>
        <p class="hero-kicker">后台首页</p>
        <h2>系统首页</h2>
        <p class="hero-text">
          当前系统已经具备毕业设计展示所需的完整基础闭环，包括登录认证、角色菜单、
          用户管理、竞赛管理、学生报名、团队组建、院级审核、获奖填报、公告发布和统计分析。
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
        <p>以下数据来自 `GET /api/auth/info`，用于驱动路由守卫与角色菜单。</p>
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
        <h3>阶段 6 可演示流程</h3>
        <p>建议先用学生账号完成报名和获奖填报，再切换管理员演示审核、公告和统计页面。</p>
      </div>

      <div class="module-grid">
        <div v-for="item in nextSteps" :key="item" class="module-item">
          {{ item }}
        </div>
      </div>
    </section>
  </div>
</template>
