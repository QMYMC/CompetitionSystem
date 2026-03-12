<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { formatDisplayText, formatRoleList, formatUserTypeLabel } from '@/utils/display'

const userStore = useUserStore()

const cards = computed(() => [
  {
    label: '登录账号',
    value: userStore.profile?.username || '--',
    tip: '当前账号信息用于识别登录身份和访问权限。',
  },
  {
    label: '当前角色',
    value: formatRoleList(userStore.roles),
    tip: '左侧菜单会根据当前角色自动展示可用功能。',
  },
  {
    label: '访问控制',
    value: '已启用',
    tip: '未登录访问管理页面时将自动跳转至登录页。',
  },
  {
    label: '系统状态',
    value: '运行正常',
    tip: '报名、获奖、公告和统计分析等功能均已接入。',
  },
])

const nextSteps = [
  '学生可完成个人赛报名、团队组建、获奖信息填报，并查看审核结果。',
  '管理人员可处理报名审核、获奖审核、公告发布和统计分析等业务。',
  '系统已覆盖报名、获奖等业务的提交、审核与反馈流程。',
]
</script>

<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div>
        <p class="hero-kicker">首页概览</p>
        <h2>系统首页</h2>
        <p class="hero-text">
          当前系统已覆盖用户管理、竞赛管理、学生报名、团队管理、院级审核、
          获奖填报、公告发布和统计分析等核心业务。
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
        <p>以下信息根据当前登录身份展示，用于控制菜单访问和页面权限。</p>
      </div>

      <div class="profile-grid">
        <div class="profile-item">
          <span>姓名</span>
          <strong>{{ formatDisplayText(userStore.profile?.realName) || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>用户名</span>
          <strong>{{ userStore.profile?.username || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>用户类型</span>
          <strong>{{ formatUserTypeLabel(userStore.profile?.userType) }}</strong>
        </div>
        <div class="profile-item">
          <span>所属学院</span>
          <strong>{{ formatDisplayText(userStore.profile?.collegeName) || '--' }}</strong>
        </div>
      </div>
    </section>

    <section class="module-panel">
      <div class="section-header">
        <h3>常用业务流程</h3>
        <p>建议按学生填报、院级审核、结果查询的顺序核验系统业务流程。</p>
      </div>

      <div class="module-grid">
        <div v-for="item in nextSteps" :key="item" class="module-item">
          {{ item }}
        </div>
      </div>
    </section>
  </div>
</template>
