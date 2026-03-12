<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const cards = computed(() => [
  {
    label: 'Current User',
    value: userStore.profile?.username || '--',
    tip: 'Authenticated through the Stage 2 login API.',
  },
  {
    label: 'Role Set',
    value: userStore.roles.join(', ') || '--',
    tip: 'Menus are filtered dynamically from route permissions.',
  },
  {
    label: 'Protected Routes',
    value: 'Enabled',
    tip: 'Unauthenticated access is redirected to /login.',
  },
  {
    label: 'Backend Status',
    value: 'Connected',
    tip: 'Axios and token interceptors are active.',
  },
])

const nextSteps = [
  'Phase 4 will implement user CRUD.',
  'Phase 5 will add registration, team, and audit workflows.',
  'Phase 6 will add awards, notices, and charts.',
]
</script>

<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div>
        <p class="hero-kicker">Admin Console</p>
        <h2>Login flow completed</h2>
        <p class="hero-text">
          You are now inside the protected admin workspace. This page confirms that routing,
          guards, Pinia user state, Axios interceptors, and backend authentication are working
          together.
        </p>
      </div>

      <div class="status-stack">
        <el-tag size="large" type="success">Authenticated</el-tag>
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
        <h3>Logged-in Profile</h3>
        <p>Profile data comes from `GET /api/auth/info` after token verification.</p>
      </div>

      <div class="profile-grid">
        <div class="profile-item">
          <span>Name</span>
          <strong>{{ userStore.profile?.realName || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>Username</span>
          <strong>{{ userStore.profile?.username || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>User Type</span>
          <strong>{{ userStore.profile?.userType || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>College</span>
          <strong>{{ userStore.profile?.collegeName || '--' }}</strong>
        </div>
      </div>
    </section>

    <section class="module-panel">
      <div class="section-header">
        <h3>Next Phases</h3>
        <p>Only phase 3 is implemented here. Business modules remain placeholders.</p>
      </div>

      <div class="module-grid">
        <div v-for="item in nextSteps" :key="item" class="module-item">
          {{ item }}
        </div>
      </div>
    </section>
  </div>
</template>
