<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createNoticeApi,
  deleteNoticeApi,
  getNoticeDetailApi,
  getNoticePageApi,
  updateNoticeApi,
} from '@/api/notices'
import { useUserStore } from '@/stores/user'
import { formatDisplayText } from '@/utils/display'

const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailLoading = ref(false)
const dialogMode = ref('create')
const currentNoticeId = ref(null)

const queryForm = reactive({
  current: 1,
  size: 10,
  keyword: '',
  publishStatus: '',
})

const pageData = reactive({
  total: 0,
  records: [],
})

const noticeForm = reactive({
  title: '',
  content: '',
  publishStatus: 'PUBLISHED',
  topFlag: 0,
})

const noticeDetail = ref(null)

const publishStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '已发布', value: 'PUBLISHED' },
  { label: '草稿', value: 'DRAFT' },
]

const isAdmin = computed(() => userStore.roles.includes('ADMIN'))

function publishStatusType(status) {
  return status === 'PUBLISHED' ? 'success' : 'info'
}

function publishStatusLabel(status) {
  return status === 'PUBLISHED' ? '已发布' : '草稿'
}

function resetForm() {
  Object.assign(noticeForm, {
    title: '',
    content: '',
    publishStatus: 'PUBLISHED',
    topFlag: 0,
  })
}

async function fetchPage() {
  loading.value = true
  try {
    const data = await getNoticePageApi(queryForm)
    pageData.total = data.total || 0
    pageData.records = data.records || []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.current = 1
  fetchPage()
}

function handleReset() {
  Object.assign(queryForm, {
    current: 1,
    size: 10,
    keyword: '',
    publishStatus: '',
  })
  fetchPage()
}

function openCreateDialog() {
  dialogMode.value = 'create'
  currentNoticeId.value = null
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(row) {
  dialogMode.value = 'edit'
  currentNoticeId.value = row.id
  const detail = await getNoticeDetailApi(row.id)
  Object.assign(noticeForm, {
    title: formatDisplayText(detail.title),
    content: formatDisplayText(detail.content),
    publishStatus: detail.publishStatus,
    topFlag: detail.topFlag,
  })
  dialogVisible.value = true
}

async function openDetail(row) {
  detailLoading.value = true
  try {
    noticeDetail.value = await getNoticeDetailApi(row.id)
  } finally {
    detailLoading.value = false
  }
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      await createNoticeApi({ ...noticeForm })
      ElMessage.success('公告已创建。')
    } else {
      await updateNoticeApi(currentNoticeId.value, { ...noticeForm })
      ElMessage.success('公告已更新。')
    }
    dialogVisible.value = false
    await fetchPage()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除公告“${row.title}”吗？`, '删除确认', { type: 'warning' })
  await deleteNoticeApi(row.id)
  ElMessage.success('公告已删除。')
  if (pageData.records.length === 1 && queryForm.current > 1) {
    queryForm.current -= 1
  }
  await fetchPage()
}

onMounted(fetchPage)
</script>

<template>
  <div class="management-page workflow-page">
    <div class="notice-layout">
      <el-card class="panel-card" shadow="hover">
        <div class="section-header">
          <h3>公告列表</h3>
          <p>管理员可发布、编辑、删除公告；其他角色可查看已发布公告详情。</p>
        </div>

        <el-form :inline="true" :model="queryForm" class="toolbar-form">
          <el-form-item label="关键词">
            <el-input v-model="queryForm.keyword" clearable placeholder="请输入公告标题关键词" />
          </el-form-item>
          <el-form-item label="发布状态">
            <el-select v-model="queryForm.publishStatus" style="width: 160px">
              <el-option
                v-for="item in publishStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button v-if="isAdmin" type="success" @click="openCreateDialog">发布公告</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="pageData.records" border>
          <el-table-column label="公告标题" min-width="220">
            <template #default="{ row }">{{ formatDisplayText(row.title) }}</template>
          </el-table-column>
          <el-table-column label="内容摘要" min-width="260" show-overflow-tooltip>
            <template #default="{ row }">{{ formatDisplayText(row.contentPreview) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="publishStatusType(row.publishStatus)">{{ publishStatusLabel(row.publishStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="置顶" width="80">
            <template #default="{ row }">
              <el-tag :type="row.topFlag ? 'warning' : 'info'">{{ row.topFlag ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="发布人" min-width="120">
            <template #default="{ row }">{{ formatDisplayText(row.publisherName) || '系统管理员' }}</template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" min-width="170" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openDetail(row)">详情</el-button>
              <el-button v-if="isAdmin" link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button v-if="isAdmin" link type="danger" @click="handleDelete(row)">删除</el-button>
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
            @current-change="fetchPage"
            @size-change="handleSearch"
          />
        </div>
      </el-card>

      <el-card class="panel-card notice-detail-card" shadow="hover">
        <div class="section-header">
          <h3>公告详情</h3>
          <p>点击列表中的“详情”可在右侧查看公告全文。</p>
        </div>

        <div v-if="noticeDetail" v-loading="detailLoading">
          <div class="notice-meta">
            <el-tag :type="publishStatusType(noticeDetail.publishStatus)">
              {{ publishStatusLabel(noticeDetail.publishStatus) }}
            </el-tag>
            <el-tag v-if="noticeDetail.topFlag" type="warning">置顶公告</el-tag>
            <el-tag type="info">{{ formatDisplayText(noticeDetail.publisherName) || '系统管理员' }}</el-tag>
          </div>
          <h3>{{ formatDisplayText(noticeDetail.title) }}</h3>
          <p class="metric-tip">发布时间：{{ noticeDetail.publishTime || '--' }}</p>
          <div class="notice-preview">{{ formatDisplayText(noticeDetail.content) }}</div>
        </div>

        <el-empty v-else description="请选择公告查看详细内容" />
      </el-card>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '发布公告' : '编辑公告'"
      width="720px"
      destroy-on-close
    >
      <el-form :model="noticeForm" label-width="90px">
        <el-form-item label="公告标题">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="noticeForm.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="noticeForm.publishStatus">
            <el-radio label="PUBLISHED">已发布</el-radio>
            <el-radio label="DRAFT">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="noticeForm.topFlag" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
