<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const title = computed(() => route.meta.title || 'Module')
const description = computed(() => route.meta.description || 'This module will be implemented later.')
</script>

<template>
  <div class="placeholder-page">
    <el-card class="module-panel" shadow="hover">
      <div class="section-header">
        <h3>{{ title }}</h3>
        <p>{{ description }}</p>
      </div>

      <div v-if="route.name === 'profile'" class="profile-grid">
        <div class="profile-item">
          <span>Real Name</span>
          <strong>{{ userStore.profile?.realName || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>Username</span>
          <strong>{{ userStore.profile?.username || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>Role List</span>
          <strong>{{ userStore.roles.join(', ') || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>College</span>
          <strong>{{ userStore.profile?.collegeName || '--' }}</strong>
        </div>
      </div>

      <el-empty
        v-else
        :description="`${title} is reserved for later phases.`"
      />
    </el-card>
  </div>
</template>
