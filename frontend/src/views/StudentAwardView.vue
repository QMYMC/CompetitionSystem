<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createAwardApi,
  getMyAwardsApi,
  getStudentAwardOptionsApi,
  updateAwardApi,
} from '@/api/awards'
import { formatDisplayText } from '@/utils/display'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('create')

const queryForm = reactive({
  keyword: '',
  auditStatus: '',
})

const options = ref([])
const awards = ref([])
const currentAwardId = ref(null)

const awardForm = reactive({
  registrationId: undefined,
  awardName: '',
  awardLevel: '',
  awardRank: '',
  awardTime: '',
  remark: '',
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

function resetForm() {
  Object.assign(awardForm, {
    registrationId: undefined,
    awardName: '',
    awardLevel: '',
    awardRank: '',
    awardTime: '',
    remark: '',
  })
}

async function fetchData() {
  loading.value = true
  try {
    const [optionData, awardData] = await Promise.all([
      getStudentAwardOptionsApi(),
      getMyAwardsApi(),
    ])
    options.value = optionData || []
    awards.value = awardData || []
  } finally {
    loading.value = false
  }
}

const filteredAwards = computed(() =>
  awards.value.filter((item) => {
    const keyword = queryForm.keyword.trim()
    const matchesKeyword =
      !keyword ||
      formatDisplayText(item.competitionTitle)?.includes(keyword) ||
      formatDisplayText(item.awardName)?.includes(keyword) ||
      formatDisplayText(item.teamName)?.includes(keyword)
    const matchesStatus = !queryForm.auditStatus || item.auditStatus === queryForm.auditStatus
    return matchesKeyword && matchesStatus
  }),
)

function openCreateDialog() {
  dialogMode.value = 'create'
  currentAwardId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  dialogMode.value = 'edit'
  currentAwardId.value = row.id
  Object.assign(awardForm, {
    registrationId: row.registrationId,
    awardName: formatDisplayText(row.awardName),
    awardLevel: formatDisplayText(row.awardLevel),
    awardRank: formatDisplayText(row.awardRank),
    awardTime: row.awardTime,
    remark: formatDisplayText(row.remark) || '',
  })
  dialogVisible.value = true
}

function canEdit(row) {
  return row.auditStatus !== 'COLLEGE_APPROVED'
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      await createAwardApi({ ...awardForm })
      ElMessage.success('获奖信息已提交，等待院级审核。')
    } else {
      await updateAwardApi(currentAwardId.value, {
        awardName: awardForm.awardName,
        awardLevel: awardForm.awardLevel,
        awardRank: awardForm.awardRank,
        awardTime: awardForm.awardTime,
        remark: awardForm.remark,
      })
      ElMessage.success('获奖信息已更新，并重新进入待审核状态。')
    }
    dialogVisible.value = false
    await fetchData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="management-page workflow-page">
    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>获奖填报</h3>
        <p>学生可基于已通过院级审核的报名记录填报获奖信息，并查看最新审核结果。</p>
      </div>

      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" clearable placeholder="竞赛名称 / 奖项名称 / 团队名称" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryForm.auditStatus" style="width: 160px">
            <el-option
              v-for="item in auditStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="openCreateDialog">新增获奖填报</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="!options.length"
        type="info"
        show-icon
        :closable="false"
        title="当前没有可用于填报获奖的已通过报名记录。"
      />

      <el-table v-loading="loading" :data="filteredAwards" border>
        <el-table-column label="竞赛名称" min-width="220">
          <template #default="{ row }">{{ formatDisplayText(row.competitionTitle) }}</template>
        </el-table-column>
        <el-table-column prop="registrationType" label="报名类型" width="100">
          <template #default="{ row }">{{ modeLabel(row.registrationType) }}</template>
        </el-table-column>
        <el-table-column label="团队名称" min-width="160">
          <template #default="{ row }">{{ formatDisplayText(row.teamName) || '--' }}</template>
        </el-table-column>
        <el-table-column label="奖项名称" min-width="160">
          <template #default="{ row }">{{ formatDisplayText(row.awardName) }}</template>
        </el-table-column>
        <el-table-column label="获奖级别" min-width="120">
          <template #default="{ row }">{{ formatDisplayText(row.awardLevel) }}</template>
        </el-table-column>
        <el-table-column label="获奖等次" min-width="120">
          <template #default="{ row }">{{ formatDisplayText(row.awardRank) }}</template>
        </el-table-column>
        <el-table-column prop="awardTime" label="获奖时间" min-width="170" />
        <el-table-column label="审核状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.auditStatus)">{{ statusLabel(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核意见" min-width="220">
          <template #default="{ row }">{{ formatDisplayText(row.latestAuditOpinion) || '--' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canEdit(row)" link type="primary" @click="openEditDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增获奖填报' : '编辑获奖信息'"
      width="640px"
      destroy-on-close
    >
      <el-form :model="awardForm" label-width="100px">
        <el-form-item label="报名记录">
          <el-select
            v-model="awardForm.registrationId"
            :disabled="dialogMode === 'edit'"
            placeholder="请选择已通过报名记录"
            style="width: 100%"
          >
            <el-option
              v-for="item in options"
              :key="item.registrationId"
              :label="`${formatDisplayText(item.competitionTitle)} / ${modeLabel(item.registrationType)}${item.teamName ? ` / ${formatDisplayText(item.teamName)}` : ''}`"
              :value="item.registrationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="奖项名称">
          <el-input v-model="awardForm.awardName" placeholder="例如：程序设计竞赛获奖证书" />
        </el-form-item>
        <el-form-item label="获奖级别">
          <el-input v-model="awardForm.awardLevel" placeholder="例如：国家级 / 省级 / 校级" />
        </el-form-item>
        <el-form-item label="获奖等次">
          <el-input v-model="awardForm.awardRank" placeholder="例如：一等奖 / 银奖 / 优秀奖" />
        </el-form-item>
        <el-form-item label="获奖时间">
          <el-date-picker
            v-model="awardForm.awardTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择获奖时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input v-model="awardForm.remark" type="textarea" :rows="4" placeholder="可填写证书编号及补充说明" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
