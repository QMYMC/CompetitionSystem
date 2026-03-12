<template>
  <div class="empty-page">
    <el-result
      icon="warning"
      title="页面不存在"
      sub-title="当前访问的页面不存在或访问地址有误，请返回系统首页。"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回系统首页</el-button>
      </template>
    </el-result>
  </div>
</template>
