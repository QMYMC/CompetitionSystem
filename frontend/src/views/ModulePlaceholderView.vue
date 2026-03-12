<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="empty-page">
    <el-result
      icon="info"
      :title="`${route.meta.title}模块预留中`"
      sub-title="当前阶段仅完成项目初始化，业务页面将在后续阶段逐步实现。"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>
