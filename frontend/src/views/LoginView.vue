<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="hover">
      <template #header>
        <div class="auth-card-header">
          <h2>登录页占位</h2>
          <el-tag type="warning">阶段 3 实现</el-tag>
        </div>
      </template>

      <p>当前仅完成工程初始化，登录表单、路由守卫和用户状态管理将在后续阶段补充。</p>
      <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
    </el-card>
  </div>
</template>
