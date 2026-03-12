<template>
  <div class="empty-page">
    <el-result
      icon="warning"
      title="页面不存在"
      sub-title="当前访问的路由尚未配置，请返回系统首页。"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>
