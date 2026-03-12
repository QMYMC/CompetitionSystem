<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createCompetitionApi,
  deleteCompetitionApi,
  getCompetitionDetailApi,
  getCompetitionOptionsApi,
  getCompetitionPageApi,
  updateCompetitionApi,
} from '@/api/competitions'
import { formatDisplayText, formatOptionList } from '@/utils/display'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref()

const queryForm = reactive({
  current: 1,
  size: 10,
  title: '',
  categoryId: undefined,
  status: '',
})

const pageData = reactive({
  total: 0,
  records: [],
})

const options = reactive({
  categories: [],
  statuses: [],
  teamModes: [],
})

const editForm = reactive({
  id: null,
  categoryId: undefined,
  title: '',
  levelName: '',
  organizer: '',
  registrationStart: '',
  registrationEnd: '',
  competitionStart: '',
  competitionEnd: '',
  teamMode: 'TEAM',
  maxTeamSize: 3,
  description: '',
  status: 'DRAFT',
})

const rules = {
  categoryId: [{ required: true, message: '请选择竞赛分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入竞赛名称', trigger: 'blur' }],
  levelName: [{ required: true, message: '请输入竞赛级别', trigger: 'blur' }],
  organizer: [{ required: true, message: '请输入主办单位', trigger: 'blur' }],
  teamMode: [{ required: true, message: '请选择参赛方式', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function resetEditForm() {
  Object.assign(editForm, {
    id: null,
    categoryId: undefined,
    title: '',
    levelName: '',
    organizer: '',
    registrationStart: '',
    registrationEnd: '',
    competitionStart: '',
    competitionEnd: '',
    teamMode: 'TEAM',
    maxTeamSize: 3,
    description: '',
    status: 'DRAFT',
  })
}

async function fetchOptions() {
  const data = await getCompetitionOptionsApi()
  options.categories = formatOptionList(data.categories || [])
  options.statuses = data.statuses || []
  options.teamModes = data.teamModes || []
}

async function fetchCompetitions() {
  loading.value = true
  try {
    const data = await getCompetitionPageApi(queryForm)
    pageData.records = data.records || []
    pageData.total = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.current = 1
  fetchCompetitions()
}

function handleReset() {
  Object.assign(queryForm, {
    current: 1,
    size: 10,
    title: '',
    categoryId: undefined,
    status: '',
  })
  fetchCompetitions()
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetEditForm()
  dialogVisible.value = true
}

async function openEditDialog(row) {
  dialogMode.value = 'edit'
  resetEditForm()
  const detail = await getCompetitionDetailApi(row.id)
  Object.assign(editForm, {
    ...detail,
    title: formatDisplayText(detail.title),
    levelName: formatDisplayText(detail.levelName),
    organizer: formatDisplayText(detail.organizer),
    description: formatDisplayText(detail.description),
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
    const payload = { ...editForm }
    delete payload.id

    if (dialogMode.value === 'create') {
      await createCompetitionApi(payload)
      ElMessage.success('竞赛新增成功')
    } else {
      await updateCompetitionApi(editForm.id, payload)
      ElMessage.success('竞赛更新成功')
    }

    dialogVisible.value = false
    fetchCompetitions()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除竞赛“${row.title}”吗？`, '删除确认', {
    type: 'warning',
  })
  await deleteCompetitionApi(row.id)
  ElMessage.success('竞赛删除成功')
  if (pageData.records.length === 1 && queryForm.current > 1) {
    queryForm.current -= 1
  }
  fetchCompetitions()
}

function formatTeamMode(value) {
  return value === 'TEAM' ? '团队赛' : '个人赛'
}

function formatStatusType(status) {
  if (status === 'PUBLISHED') return 'success'
  if (status === 'CLOSED') return 'info'
  return 'warning'
}

function formatStatusLabel(status) {
  if (status === 'PUBLISHED') return '已发布'
  if (status === 'CLOSED') return '已关闭'
  return '草稿'
}

onMounted(async () => {
  await fetchOptions()
  await fetchCompetitions()
})
</script>

<template>
  <div class="management-page">
    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>竞赛管理</h3>
        <p>管理员可维护竞赛分类、赛制、报名时间、比赛时间和发布状态。</p>
      </div>

      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="竞赛名称">
          <el-input v-model="queryForm.title" clearable placeholder="请输入竞赛名称" />
        </el-form-item>
        <el-form-item label="竞赛分类">
          <el-select v-model="queryForm.categoryId" clearable placeholder="全部分类" style="width: 180px">
            <el-option
              v-for="item in options.categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 160px">
            <el-option
              v-for="item in options.statuses"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreateDialog">新增竞赛</el-button>
        </el-form-item>
      </el-form>

        <el-table v-loading="loading" :data="pageData.records" border>
        <el-table-column label="竞赛名称" min-width="220">
          <template #default="{ row }">{{ formatDisplayText(row.title) }}</template>
        </el-table-column>
        <el-table-column label="竞赛分类" min-width="150">
          <template #default="{ row }">{{ formatDisplayText(row.categoryName) }}</template>
        </el-table-column>
        <el-table-column label="竞赛级别" min-width="120">
          <template #default="{ row }">{{ formatDisplayText(row.levelName) }}</template>
        </el-table-column>
        <el-table-column label="主办单位" min-width="180">
          <template #default="{ row }">{{ formatDisplayText(row.organizer) }}</template>
        </el-table-column>
        <el-table-column prop="teamMode" label="参赛方式" width="100">
          <template #default="{ row }">{{ formatTeamMode(row.teamMode) }}</template>
        </el-table-column>
        <el-table-column prop="registrationStart" label="报名开始" min-width="170" />
        <el-table-column prop="registrationEnd" label="报名结束" min-width="170" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="formatStatusType(row.status)">{{ formatStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
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
          @current-change="fetchCompetitions"
          @size-change="handleSearch"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增竞赛' : '编辑竞赛'"
      width="760px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="100px">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="竞赛分类" prop="categoryId">
              <el-select v-model="editForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in options.categories"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="竞赛级别" prop="levelName">
              <el-input v-model="editForm.levelName" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="竞赛名称" prop="title">
              <el-input v-model="editForm.title" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="主办单位" prop="organizer">
              <el-input v-model="editForm.organizer" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参赛方式" prop="teamMode">
              <el-select v-model="editForm.teamMode" style="width: 100%">
                <el-option
                  v-for="item in options.teamModes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="团队人数上限">
              <el-input-number v-model="editForm.maxTeamSize" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名开始">
              <el-date-picker
                v-model="editForm.registrationStart"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名结束">
              <el-date-picker
                v-model="editForm.registrationEnd"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="竞赛开始时间">
              <el-date-picker
                v-model="editForm.competitionStart"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="竞赛结束时间">
              <el-date-picker
                v-model="editForm.competitionEnd"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="editForm.status" style="width: 100%">
                <el-option
                  v-for="item in options.statuses"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="竞赛说明">
              <el-input v-model="editForm.description" type="textarea" :rows="4" />
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
