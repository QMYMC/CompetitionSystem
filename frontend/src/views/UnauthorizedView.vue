<template>
  <div class="empty-page">
    <el-result
      icon="error"
      title="No access"
      sub-title="Your current role does not have permission to visit this page."
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">Back to dashboard</el-button>
      </template>
    </el-result>
  </div>
</template>
