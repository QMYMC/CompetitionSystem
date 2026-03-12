<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const title = computed(() => route.meta.title || '功能模块开发中')
const description = computed(() => route.meta.description || '该模块将在后续阶段逐步实现。')
</script>

<template>
  <div class="placeholder-page">
    <el-card class="module-panel" shadow="hover">
      <div class="section-header">
        <h3>功能模块开发中</h3>
        <p>{{ description }}</p>
      </div>

      <div v-if="route.name === 'profile'" class="profile-grid">
        <div class="profile-item">
          <span>姓名</span>
          <strong>{{ userStore.profile?.realName || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>用户名</span>
          <strong>{{ userStore.profile?.username || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>角色列表</span>
          <strong>{{ userStore.roles.join(', ') || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>所属学院</span>
          <strong>{{ userStore.profile?.collegeName || '--' }}</strong>
        </div>
      </div>

      <el-empty
        v-else
        :description="`${title} 模块正在开发中，将在后续阶段补充。`"
      />
    </el-card>
  </div>
</template>
