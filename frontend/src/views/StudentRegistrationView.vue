<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getMyRegistrationsApi,
  getStudentCompetitionsApi,
  submitIndividualRegistrationApi,
} from '@/api/workflow'

const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const selectedCompetition = ref(null)

const queryForm = reactive({
  title: '',
  teamMode: '',
})

const competitions = ref([])
const registrations = ref([])

const individualForm = reactive({
  competitionId: null,
  remark: '',
})

const teamModeOptions = [
  { label: '全部赛制', value: '' },
  { label: '个人赛', value: 'INDIVIDUAL' },
  { label: '团队赛', value: 'TEAM' },
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
  return '未报名'
}

function modeLabel(mode) {
  return mode === 'TEAM' ? '团队赛' : '个人赛'
}

function canSubmitIndividual(row) {
  return row.teamMode === 'INDIVIDUAL'
}

async function fetchData() {
  loading.value = true
  try {
    const [competitionData, registrationData] = await Promise.all([
      getStudentCompetitionsApi(queryForm),
      getMyRegistrationsApi(),
    ])
    competitions.value = competitionData || []
    registrations.value = registrationData || []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  fetchData()
}

function handleReset() {
  queryForm.title = ''
  queryForm.teamMode = ''
  fetchData()
}

function openIndividualDialog(row) {
  selectedCompetition.value = row
  individualForm.competitionId = row.id
  individualForm.remark =
    row.myAuditStatus === 'COLLEGE_REJECTED'
      ? '已根据驳回意见补充说明，申请重新提交。'
      : ''
  dialogVisible.value = true
}

async function handleSubmitIndividual() {
  submitLoading.value = true
  try {
    await submitIndividualRegistrationApi({ ...individualForm })
    ElMessage.success('个人赛报名已提交，等待院级审核。')
    dialogVisible.value = false
    await fetchData()
  } finally {
    submitLoading.value = false
  }
}

function goTeamManagement(row) {
  router.push({
    name: 'student-teams',
    query: { competitionId: row.id },
  })
}

onMounted(fetchData)
</script>

<template>
  <div class="management-page workflow-page">
    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>开放竞赛列表</h3>
        <p>学生可在此查看当前开放报名的个人赛和团队赛，并进入相应报名流程。</p>
      </div>

      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="竞赛名称">
          <el-input v-model="queryForm.title" clearable placeholder="请输入竞赛名称" />
        </el-form-item>
        <el-form-item label="赛制">
          <el-select v-model="queryForm.teamMode" style="width: 160px">
            <el-option
              v-for="item in teamModeOptions"
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

      <el-table v-loading="loading" :data="competitions" border>
        <el-table-column prop="title" label="竞赛名称" min-width="220" />
        <el-table-column prop="categoryName" label="分类" min-width="120" />
        <el-table-column prop="levelName" label="级别" min-width="100" />
        <el-table-column prop="teamMode" label="赛制" width="100">
          <template #default="{ row }">{{ modeLabel(row.teamMode) }}</template>
        </el-table-column>
        <el-table-column prop="registrationStart" label="报名开始" min-width="170" />
        <el-table-column prop="registrationEnd" label="报名结束" min-width="170" />
        <el-table-column label="当前状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.myAuditStatus)">{{ statusLabel(row.myAuditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canSubmitIndividual(row)"
              link
              type="primary"
              @click="openIndividualDialog(row)"
            >
              {{ row.myAuditStatus === 'COLLEGE_REJECTED' ? '重新提交个人赛' : '个人赛报名' }}
            </el-button>
            <el-button
              v-else
              link
              type="primary"
              @click="goTeamManagement(row)"
            >
              {{ row.myTeamId ? '查看团队' : '创建团队' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>我的报名记录</h3>
        <p>这里展示个人赛和团队赛的提交结果，以及最新院级审核意见。</p>
      </div>

      <el-table v-loading="loading" :data="registrations" border>
        <el-table-column prop="competitionTitle" label="竞赛名称" min-width="220" />
        <el-table-column prop="registrationType" label="报名类型" width="100">
          <template #default="{ row }">{{ modeLabel(row.registrationType) }}</template>
        </el-table-column>
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
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="个人赛报名"
      width="520px"
      destroy-on-close
    >
      <el-form :model="individualForm" label-width="90px">
        <el-form-item label="竞赛名称">
          <span>{{ selectedCompetition?.title }}</span>
        </el-form-item>
        <el-form-item label="报名说明">
          <el-input
            v-model="individualForm.remark"
            type="textarea"
            :rows="4"
            placeholder="可填写补充说明，便于演示审核流程。"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitIndividual">
          提交报名
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
