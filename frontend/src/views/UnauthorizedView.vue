<template>
  <div class="empty-page">
    <el-result
      icon="error"
      title="无权限访问"
      sub-title="当前账号角色没有访问该页面的权限，请返回系统首页。"
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回系统首页</el-button>
      </template>
    </el-result>
  </div>
</template>
