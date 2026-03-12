<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createUserApi,
  deleteUserApi,
  getUserDetailApi,
  getUserOptionsApi,
  getUserPageApi,
  updateUserApi,
} from '@/api/users'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref()

const queryForm = reactive({
  current: 1,
  size: 10,
  username: '',
  realName: '',
  status: undefined,
  roleId: undefined,
})

const pageData = reactive({
  total: 0,
  records: [],
})

const options = reactive({
  roles: [],
  colleges: [],
})

const editForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  gender: 'M',
  roleId: undefined,
  collegeId: undefined,
  status: 1,
  remark: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    {
      validator: (_, value, callback) => {
        if (dialogMode.value === 'create' && !value) {
          callback(new Error('请输入初始密码'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

function resetEditForm() {
  Object.assign(editForm, {
    id: null,
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    gender: 'M',
    roleId: undefined,
    collegeId: undefined,
    status: 1,
    remark: '',
  })
}

async function fetchOptions() {
  const data = await getUserOptionsApi()
  options.roles = data.roles || []
  options.colleges = data.colleges || []
}

async function fetchUsers() {
  loading.value = true
  try {
    const data = await getUserPageApi(queryForm)
    pageData.records = data.records || []
    pageData.total = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.current = 1
  fetchUsers()
}

function handleReset() {
  Object.assign(queryForm, {
    current: 1,
    size: 10,
    username: '',
    realName: '',
    status: undefined,
    roleId: undefined,
  })
  fetchUsers()
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetEditForm()
  dialogVisible.value = true
}

async function openEditDialog(row) {
  dialogMode.value = 'edit'
  resetEditForm()
  const detail = await getUserDetailApi(row.id)
  Object.assign(editForm, {
    id: detail.id,
    username: detail.username,
    realName: detail.realName,
    email: detail.email,
    phone: detail.phone,
    gender: detail.gender || 'M',
    roleId: detail.roleId,
    collegeId: detail.collegeId,
    status: detail.status ?? 1,
    remark: detail.remark,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  submitLoading.value = true
  try {
    const payload = {
      username: editForm.username,
      password: editForm.password,
      realName: editForm.realName,
      email: editForm.email,
      phone: editForm.phone,
      gender: editForm.gender,
      roleId: editForm.roleId,
      collegeId: editForm.collegeId,
      status: editForm.status,
      remark: editForm.remark,
    }

    if (dialogMode.value === 'create') {
      await createUserApi(payload)
      ElMessage.success('用户新增成功')
    } else {
      await updateUserApi(editForm.id, payload)
      ElMessage.success('用户更新成功')
    }

    dialogVisible.value = false
    fetchUsers()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除用户「${row.username}」吗？`, '删除确认', {
    type: 'warning',
  })
  await deleteUserApi(row.id)
  ElMessage.success('用户删除成功')
  if (pageData.records.length === 1 && queryForm.current > 1) {
    queryForm.current -= 1
  }
  fetchUsers()
}

function formatStatus(status) {
  return status === 1 ? '启用' : '停用'
}

function formatGender(gender) {
  if (gender === 'F') return '女'
  if (gender === 'M') return '男'
  return gender || '--'
}

onMounted(async () => {
  await fetchOptions()
  await fetchUsers()
})
</script>

<template>
  <div class="management-page">
    <el-card class="panel-card" shadow="hover">
      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" clearable placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryForm.realName" clearable placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryForm.roleId" clearable placeholder="全部角色" style="width: 180px">
            <el-option
              v-for="item in options.roles"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 160px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreateDialog">新增用户</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="pageData.records" border>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="roleName" label="角色" min-width="140" />
        <el-table-column prop="collegeName" label="学院" min-width="180" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ formatGender(row.gender) }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ formatStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="queryForm.current"
          v-model:page-size="queryForm.size"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50]"
          :total="pageData.total"
          @current-change="fetchUsers"
          @size-change="handleSearch"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增用户' : '编辑用户'"
      width="680px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="90px">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="editForm.username" :disabled="dialogMode === 'edit'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="dialogMode === 'create' ? '初始密码' : '重置密码'" prop="password">
              <el-input v-model="editForm.password" show-password placeholder="编辑时可留空" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="editForm.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleId">
              <el-select v-model="editForm.roleId" placeholder="请选择角色" style="width: 100%">
                <el-option
                  v-for="item in options.roles"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学院">
              <el-select v-model="editForm.collegeId" clearable placeholder="请选择学院" style="width: 100%">
                <el-option
                  v-for="item in options.colleges"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="editForm.status" style="width: 100%">
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="editForm.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="editForm.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="editForm.gender" style="width: 100%">
                <el-option label="男" value="M" />
                <el-option label="女" value="F" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="editForm.remark" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
