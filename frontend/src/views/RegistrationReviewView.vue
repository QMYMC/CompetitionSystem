<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  approveRegistrationApi,
  getRegistrationReviewPageApi,
  rejectRegistrationApi,
} from '@/api/workflow'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const reviewMode = ref('approve')
const currentRow = ref(null)

const queryForm = reactive({
  current: 1,
  size: 10,
  auditStatus: 'PENDING_COLLEGE_REVIEW',
  registrationType: '',
})

const pageData = reactive({
  total: 0,
  records: [],
})

const reviewForm = reactive({
  opinion: '',
})

const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '待院级审核', value: 'PENDING_COLLEGE_REVIEW' },
  { label: '院级通过', value: 'COLLEGE_APPROVED' },
  { label: '院级驳回', value: 'COLLEGE_REJECTED' },
]

const typeOptions = [
  { label: '全部类型', value: '' },
  { label: '个人赛', value: 'INDIVIDUAL' },
  { label: '团队赛', value: 'TEAM' },
]

function modeLabel(mode) {
  return mode === 'TEAM' ? '团队赛' : '个人赛'
}

function statusType(status) {
  if (status === 'COLLEGE_APPROVED') return 'success'
  if (status === 'COLLEGE_REJECTED') return 'danger'
  if (status === 'PENDING_COLLEGE_REVIEW') return 'warning'
  return 'info'
}

function statusLabel(status) {
  if (status === 'COLLEGE_APPROVED') return '院级通过'
  if (status === 'COLLEGE_REJECTED') return '院级驳回'
  if (status === 'PENDING_COLLEGE_REVIEW') return '待院级审核'
  return status || '--'
}

async function fetchPage() {
  loading.value = true
  try {
    const data = await getRegistrationReviewPageApi(queryForm)
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
    auditStatus: 'PENDING_COLLEGE_REVIEW',
    registrationType: '',
  })
  fetchPage()
}

function openReviewDialog(row, mode) {
  currentRow.value = row
  reviewMode.value = mode
  reviewForm.opinion = mode === 'approve' ? '材料完整，同意通过。' : '请根据院级审核意见补充后再次提交。'
  dialogVisible.value = true
}

async function handleReview() {
  if (!currentRow.value) {
    return
  }
  submitLoading.value = true
  try {
    if (reviewMode.value === 'approve') {
      await approveRegistrationApi(currentRow.value.id, { opinion: reviewForm.opinion })
      ElMessage.success('审核已通过。')
    } else {
      await rejectRegistrationApi(currentRow.value.id, { opinion: reviewForm.opinion })
      ElMessage.success('审核已驳回。')
    }
    dialogVisible.value = false
    await fetchPage()
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchPage)
</script>

<template>
  <div class="management-page workflow-page">
    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>院级审核中心</h3>
        <p>管理员和学院审核员可在此查看学生报名，填写审核意见，并完成通过或驳回。</p>
      </div>

      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="审核状态">
          <el-select v-model="queryForm.auditStatus" style="width: 180px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报名类型">
          <el-select v-model="queryForm.registrationType" style="width: 160px">
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="pageData.records" border>
        <el-table-column prop="competitionTitle" label="竞赛名称" min-width="220" />
        <el-table-column prop="registrationType" label="报名类型" width="100">
          <template #default="{ row }">{{ modeLabel(row.registrationType) }}</template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人/队长" min-width="120" />
        <el-table-column prop="teamName" label="团队名称" min-width="160" />
        <el-table-column prop="teacherName" label="指导教师" min-width="120" />
        <el-table-column prop="memberSummary" label="成员名单" min-width="220" />
        <el-table-column label="审核状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.auditStatus)">{{ statusLabel(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="latestAuditOpinion" label="审核意见" min-width="220" />
        <el-table-column prop="submitTime" label="提交时间" min-width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.auditStatus === 'PENDING_COLLEGE_REVIEW'"
              link
              type="success"
              @click="openReviewDialog(row, 'approve')"
            >
              通过
            </el-button>
            <el-button
              v-if="row.auditStatus === 'PENDING_COLLEGE_REVIEW'"
              link
              type="danger"
              @click="openReviewDialog(row, 'reject')"
            >
              驳回
            </el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="reviewMode === 'approve' ? '审核通过' : '审核驳回'"
      width="520px"
    >
      <el-form :model="reviewForm" label-width="90px">
        <el-form-item label="竞赛名称">
          <span>{{ currentRow?.competitionTitle }}</span>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="reviewForm.opinion"
            type="textarea"
            :rows="4"
            placeholder="请输入院级审核意见"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleReview">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
