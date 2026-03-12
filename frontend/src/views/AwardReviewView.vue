<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  approveAwardApi,
  getAwardReviewPageApi,
  rejectAwardApi,
} from '@/api/awards'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const reviewMode = ref('approve')
const currentRow = ref(null)

const queryForm = reactive({
  current: 1,
  size: 10,
  auditStatus: 'PENDING_COLLEGE_REVIEW',
  keyword: '',
})

const pageData = reactive({
  total: 0,
  records: [],
})

const reviewForm = reactive({
  opinion: '',
})

const auditStatusOptions = [
  { label: '全部状态', value: '' },
  { label: '待院级审核', value: 'PENDING_COLLEGE_REVIEW' },
  { label: '院级通过', value: 'COLLEGE_APPROVED' },
  { label: '院级驳回', value: 'COLLEGE_REJECTED' },
]

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

function modeLabel(mode) {
  return mode === 'TEAM' ? '团队赛' : '个人赛'
}

async function fetchPage() {
  loading.value = true
  try {
    const data = await getAwardReviewPageApi(queryForm)
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
    keyword: '',
  })
  fetchPage()
}

function openReviewDialog(row, mode) {
  currentRow.value = row
  reviewMode.value = mode
  reviewForm.opinion =
    mode === 'approve'
      ? '获奖材料完整，院级审核通过。'
      : '获奖材料待补充，请根据审核意见修改后重新提交。'
  dialogVisible.value = true
}

async function handleReview() {
  if (!currentRow.value) {
    return
  }
  submitLoading.value = true
  try {
    if (reviewMode.value === 'approve') {
      await approveAwardApi(currentRow.value.id, { opinion: reviewForm.opinion })
      ElMessage.success('获奖信息已审核通过。')
    } else {
      await rejectAwardApi(currentRow.value.id, { opinion: reviewForm.opinion })
      ElMessage.success('获奖信息已驳回。')
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
        <h3>院级获奖审核</h3>
        <p>管理员和院级审核员可在此查看学生填报的获奖信息，并执行通过或驳回。</p>
      </div>

      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="审核状态">
          <el-select v-model="queryForm.auditStatus" style="width: 180px">
            <el-option
              v-for="item in auditStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" clearable placeholder="竞赛名称 / 申请人 / 奖项名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="pageData.records" border>
        <el-table-column prop="competitionTitle" label="竞赛名称" min-width="220" />
        <el-table-column prop="applicantName" label="申请人 / 队长" min-width="120" />
        <el-table-column prop="registrationType" label="报名类型" width="100">
          <template #default="{ row }">{{ modeLabel(row.registrationType) }}</template>
        </el-table-column>
        <el-table-column prop="teamName" label="团队名称" min-width="150" />
        <el-table-column prop="awardName" label="奖项名称" min-width="160" />
        <el-table-column prop="awardLevel" label="获奖级别" min-width="120" />
        <el-table-column prop="awardRank" label="获奖等次" min-width="120" />
        <el-table-column prop="awardTime" label="获奖时间" min-width="170" />
        <el-table-column label="审核状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.auditStatus)">{{ statusLabel(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="latestAuditOpinion" label="审核意见" min-width="220" />
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
        <el-form-item label="奖项名称">
          <span>{{ currentRow?.awardName }}</span>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.opinion" type="textarea" :rows="4" placeholder="请输入院级审核意见" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleReview">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>
