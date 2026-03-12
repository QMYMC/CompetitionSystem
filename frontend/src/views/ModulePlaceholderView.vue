<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { formatDisplayText, formatRoleList } from '@/utils/display'

const route = useRoute()
const userStore = useUserStore()

const title = computed(() => route.meta.title || '功能信息')
const description = computed(() => route.meta.description || '相关功能信息待补充。')
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
          <span>姓名</span>
          <strong>{{ formatDisplayText(userStore.profile?.realName) || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>用户名</span>
          <strong>{{ userStore.profile?.username || '--' }}</strong>
        </div>
        <div class="profile-item">
          <span>角色列表</span>
          <strong>{{ formatRoleList(userStore.roles) }}</strong>
        </div>
        <div class="profile-item">
          <span>所属学院</span>
          <strong>{{ formatDisplayText(userStore.profile?.collegeName) || '--' }}</strong>
        </div>
      </div>

      <el-empty
        v-else
        :description="`${title}相关信息暂未开放。`"
      />
    </el-card>
  </div>
</template>
