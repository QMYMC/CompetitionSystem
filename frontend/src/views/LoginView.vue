<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const loading = ref(false)
const formRef = ref()
const loginForm = reactive({
  username: 'admin',
  password: 'Admin@123',
})

const rules = {
  username: [{ required: true, message: '请输入用户名。', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码。', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功。')
    await router.push(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-shell">
    <section class="login-copy">
      <p class="hero-kicker">阶段 3</p>
      <h1>{{ appStore.systemName }}</h1>
      <p class="hero-text">
        当前前端基础框架已经和阶段 2 的后端登录接口联通。可直接使用默认管理员账号完成登录，
        进入后台管理首页。
      </p>

      <div class="login-tips">
        <div class="login-tip-card">
          <span>后端登录接口</span>
          <strong>POST /api/auth/login</strong>
        </div>
        <div class="login-tip-card">
          <span>默认管理员账号</span>
          <strong>admin / Admin@123</strong>
        </div>
      </div>
    </section>

    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="auth-card-header">
          <div>
            <h2>系统登录</h2>
            <p>登录后进入高校学科竞赛信息管理系统后台。</p>
          </div>
          <el-tag type="success">接口已联通</el-tag>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" :prefix-icon="User" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            :prefix-icon="Lock"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>

        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
          立即登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>
