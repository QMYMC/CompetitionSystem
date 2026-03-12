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

const demoAccounts = [
  '管理员：admin / Admin@123',
  '学生演示：student03 / Student@123',
  '团队成员：student04 / Student@123',
]

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    await router.push(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-shell">
    <section class="login-copy">
      <p class="hero-kicker">阶段 6</p>
      <h1>{{ appStore.systemName }}</h1>
      <p class="hero-text">
        当前系统已完成报名、团队、院级审核、获奖填报、公告管理和统计分析闭环。
        可使用管理员与学生账号分别演示学生端流程和管理端流程。
      </p>

      <div class="login-tips">
        <div class="login-tip-card">
          <span>登录接口</span>
          <strong>POST /api/auth/login</strong>
        </div>
        <div class="login-tip-card">
          <span>推荐演示账号</span>
          <strong>{{ demoAccounts[0] }}</strong>
        </div>
      </div>

      <el-card class="panel-card" shadow="never">
        <div class="section-header">
          <h3>推荐演示账号</h3>
          <p>管理员负责审核、公告和统计展示，学生负责报名、团队和获奖填报演示。</p>
        </div>
        <div class="module-grid">
          <div v-for="item in demoAccounts" :key="item" class="module-item">
            {{ item }}
          </div>
        </div>
      </el-card>
    </section>

    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="auth-card-header">
          <div>
            <h2>系统登录</h2>
            <p>登录后进入后台首页，并根据当前角色展示相应菜单和功能。</p>
          </div>
          <el-tag type="success">前后端已联调</el-tag>
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
