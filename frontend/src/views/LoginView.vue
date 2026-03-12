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

const accountList = [
  '系统管理员：admin / Admin@123',
  '学生账号：student03 / Student@123',
  '学生成员：student04 / Student@123',
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
      <p class="hero-kicker">统一登录入口</p>
      <h1>{{ appStore.systemName }}</h1>
      <p class="hero-text">
        系统支持竞赛管理、报名审核、团队管理、获奖填报、公告发布和统计分析等业务。
        不同角色登录后将自动加载对应的菜单与功能权限。
      </p>

      <div class="login-tips">
        <div class="login-tip-card">
          <span>登录方式</span>
          <strong>账号密码认证</strong>
        </div>
        <div class="login-tip-card">
          <span>常用账号</span>
          <strong>{{ accountList[0] }}</strong>
        </div>
      </div>

      <el-card class="panel-card" shadow="never">
        <div class="section-header">
          <h3>角色账号</h3>
          <p>可使用下列账号进入不同角色界面，核验对应的业务权限与功能范围。</p>
        </div>
        <div class="module-grid">
          <div v-for="item in accountList" :key="item" class="module-item">
            {{ item }}
          </div>
        </div>
      </el-card>
    </section>

    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="auth-card-header">
          <div>
            <h2>用户登录</h2>
            <p>登录后进入系统首页，并根据当前角色加载对应菜单和功能。</p>
          </div>
          <el-tag type="success">服务已连接</el-tag>
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
          登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>
