<script setup>
const metrics = [
  { label: '竞赛总数', value: '--', tip: '阶段 2 接入数据库后补齐' },
  { label: '报名人次', value: '--', tip: '阶段 4 开始联调业务数据' },
  { label: '获奖数量', value: '--', tip: '阶段 6 接入获奖模块' },
  { label: '待审核事项', value: '--', tip: '阶段 5 接入审核流程' },
]

const modules = [
  '用户与角色管理',
  '竞赛信息管理',
  '报名与组队',
  '院级审核',
  '获奖信息管理',
  '公告通知',
  '统计分析大屏',
]
</script>

<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div>
        <p class="hero-kicker">Competition Admin</p>
        <h2>高校学科竞赛信息管理系统</h2>
        <p class="hero-text">
          当前页面用于验证前端工程、路由、Pinia、Element Plus 和基础布局已完成初始化。
        </p>
      </div>
      <el-tag size="large" type="success">阶段 1 已完成初始化</el-tag>
    </section>

    <section class="metric-grid">
      <el-card v-for="metric in metrics" :key="metric.label" shadow="hover" class="metric-card">
        <p class="metric-label">{{ metric.label }}</p>
        <h3 class="metric-value">{{ metric.value }}</h3>
        <p class="metric-tip">{{ metric.tip }}</p>
      </el-card>
    </section>

    <section class="module-panel">
      <div class="section-header">
        <h3>预留业务模块</h3>
        <p>目录结构已经按后续开发方向整理，可以在下一阶段直接扩展。</p>
      </div>
      <div class="module-grid">
        <div v-for="module in modules" :key="module" class="module-item">
          {{ module }}
        </div>
      </div>
    </section>
  </div>
</template>
