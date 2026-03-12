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
  username: [{ required: true, message: 'Please enter username.', trigger: 'blur' }],
  password: [{ required: true, message: 'Please enter password.', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('Login successful.')
    await router.push(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-shell">
    <section class="login-copy">
      <p class="hero-kicker">Phase 3</p>
      <h1>{{ appStore.systemName }}</h1>
      <p class="hero-text">
        Frontend framework is now connected to the Stage 2 backend. Use the default admin
        account to complete the login flow and enter the management console.
      </p>

      <div class="login-tips">
        <div class="login-tip-card">
          <span>Backend API</span>
          <strong>`POST /api/auth/login`</strong>
        </div>
        <div class="login-tip-card">
          <span>Default Admin</span>
          <strong>admin / Admin@123</strong>
        </div>
      </div>
    </section>

    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="auth-card-header">
          <div>
            <h2>System Login</h2>
            <p>Sign in to continue to the admin console.</p>
          </div>
          <el-tag type="success">API Connected</el-tag>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="Username" prop="username">
          <el-input v-model="loginForm.username" :prefix-icon="User" placeholder="admin" />
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input
            v-model="loginForm.password"
            :prefix-icon="Lock"
            show-password
            placeholder="Admin@123"
          />
        </el-form-item>

        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
          Sign In
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>
