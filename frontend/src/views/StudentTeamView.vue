<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addTeamMemberApi,
  createTeamApi,
  getMyTeamDetailApi,
  getMyTeamsApi,
  getTeamFormOptionsApi,
  removeTeamMemberApi,
  submitTeamApi,
  updateTeamApi,
} from '@/api/workflow'
import { formatDisplayText, formatOptionList } from '@/utils/display'

const route = useRoute()

const loading = ref(false)
const submitLoading = ref(false)
const detailLoading = ref(false)
const dialogVisible = ref(false)
const memberDialogVisible = ref(false)
const submitDialogVisible = ref(false)
const dialogMode = ref('create')
const selectedTeamId = ref(null)
const teamDetail = ref(null)

const teams = ref([])
const options = reactive({
  competitions: [],
  teachers: [],
  students: [],
})

const teamForm = reactive({
  id: null,
  competitionId: undefined,
  teamName: '',
  teacherId: undefined,
  memberUserIds: [],
  remark: '',
})

const memberForm = reactive({
  userId: undefined,
})

const submitForm = reactive({
  remark: '',
})

const editableStatuses = ['FORMING', 'COLLEGE_REJECTED']

function teamStatusType(status) {
  if (status === 'COLLEGE_APPROVED') return 'success'
  if (status === 'COLLEGE_REJECTED') return 'danger'
  if (status === 'PENDING_COLLEGE_REVIEW') return 'warning'
  return 'info'
}

function teamStatusLabel(status) {
  if (status === 'COLLEGE_APPROVED') return '院级通过'
  if (status === 'COLLEGE_REJECTED') return '院级驳回'
  if (status === 'PENDING_COLLEGE_REVIEW') return '待院级审核'
  return '组队中'
}

function currentTeamEditable(team = teamDetail.value) {
  return team && editableStatuses.includes(team.teamStatus)
}

async function fetchOptions() {
  const data = await getTeamFormOptionsApi()
  options.competitions = formatOptionList(data.competitions || [])
  options.teachers = formatOptionList(data.teachers || [])
  options.students = formatOptionList(data.students || [])
}

async function fetchTeams() {
  loading.value = true
  try {
    teams.value = await getMyTeamsApi()
    if (selectedTeamId.value) {
      const exists = teams.value.some((item) => item.id === selectedTeamId.value)
      if (!exists) {
        selectedTeamId.value = null
      }
    }
    if (!selectedTeamId.value && teams.value.length) {
      selectedTeamId.value = teams.value[0].id
    }
    if (selectedTeamId.value) {
      await fetchTeamDetail(selectedTeamId.value)
    } else {
      teamDetail.value = null
    }
  } finally {
    loading.value = false
  }
}

async function fetchTeamDetail(teamId) {
  detailLoading.value = true
  try {
    teamDetail.value = await getMyTeamDetailApi(teamId)
    selectedTeamId.value = teamId
  } finally {
    detailLoading.value = false
  }
}

function resetTeamForm() {
  Object.assign(teamForm, {
    id: null,
    competitionId: route.query.competitionId ? Number(route.query.competitionId) : undefined,
    teamName: '',
    teacherId: undefined,
    memberUserIds: [],
    remark: '',
  })
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetTeamForm()
  dialogVisible.value = true
}

async function openEditDialog(team) {
  dialogMode.value = 'edit'
  const detail = await getMyTeamDetailApi(team.id)
  Object.assign(teamForm, {
    id: detail.id,
    competitionId: detail.competitionId,
    teamName: formatDisplayText(detail.teamName),
    teacherId: detail.teacherId,
    memberUserIds: [],
    remark: formatDisplayText(detail.remark) || '',
  })
  dialogVisible.value = true
}

async function handleSubmitTeamForm() {
  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      await createTeamApi({ ...teamForm })
      ElMessage.success('团队创建成功。')
    } else {
      await updateTeamApi(teamForm.id, {
        teamName: teamForm.teamName,
        teacherId: teamForm.teacherId,
        remark: teamForm.remark,
      })
      ElMessage.success('团队信息已更新。')
    }
    dialogVisible.value = false
    await fetchTeams()
  } finally {
    submitLoading.value = false
  }
}

function openMemberDialog() {
  memberForm.userId = undefined
  memberDialogVisible.value = true
}

async function handleAddMember() {
  if (!teamDetail.value || !memberForm.userId) {
    return
  }
  submitLoading.value = true
  try {
    await addTeamMemberApi(teamDetail.value.id, { userId: memberForm.userId })
    ElMessage.success('团队成员已添加。')
    memberDialogVisible.value = false
    await fetchTeams()
  } finally {
    submitLoading.value = false
  }
}

async function handleRemoveMember(member) {
  if (!teamDetail.value) {
    return
  }
  await ElMessageBox.confirm(`确认移出成员“${formatDisplayText(member.realName)}”吗？`, '移除确认', { type: 'warning' })
  await removeTeamMemberApi(teamDetail.value.id, member.userId)
  ElMessage.success('团队成员已移除。')
  await fetchTeams()
}

function openSubmitDialog(team) {
  selectedTeamId.value = team.id
  submitForm.remark = team.remark || '提交院级审核'
  submitDialogVisible.value = true
}

async function handleSubmitForReview() {
  submitLoading.value = true
  try {
    await submitTeamApi(selectedTeamId.value, { remark: submitForm.remark })
    ElMessage.success('团队报名已提交，等待院级审核。')
    submitDialogVisible.value = false
    await fetchTeams()
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await fetchOptions()
  await fetchTeams()
})
</script>

<template>
  <div class="management-page workflow-page">
    <el-row :gutter="20">
      <el-col :xl="14" :lg="13" :md="24">
        <el-card class="panel-card" shadow="hover">
          <div class="section-header">
            <h3>我的团队</h3>
            <p>学生可在此创建团队、维护指导教师和成员，并提交院级审核。</p>
          </div>

          <div class="toolbar-actions">
            <el-button type="primary" @click="openCreateDialog">创建团队</el-button>
          </div>

          <el-table v-loading="loading" :data="teams" border @row-click="(row) => fetchTeamDetail(row.id)">
            <el-table-column label="竞赛名称" min-width="220">
              <template #default="{ row }">{{ formatDisplayText(row.competitionTitle) }}</template>
            </el-table-column>
            <el-table-column label="团队名称" min-width="160">
              <template #default="{ row }">{{ formatDisplayText(row.teamName) }}</template>
            </el-table-column>
            <el-table-column label="团队状态" width="140">
              <template #default="{ row }">
                <el-tag :type="teamStatusType(row.teamStatus)">{{ teamStatusLabel(row.teamStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="指导教师" min-width="120">
              <template #default="{ row }">{{ formatDisplayText(row.teacherName) || '--' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click.stop="fetchTeamDetail(row.id)">详情</el-button>
                <el-button
                  v-if="currentTeamEditable(row)"
                  link
                  type="primary"
                  @click.stop="openEditDialog(row)"
                >
                  编辑
                </el-button>
                <el-button
                  v-if="currentTeamEditable(row)"
                  link
                  type="success"
                  @click.stop="openSubmitDialog(row)"
                >
                  提交审核
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xl="10" :lg="11" :md="24">
        <el-card class="panel-card" shadow="hover">
          <div class="section-header">
            <h3>团队详情</h3>
            <p v-if="teamDetail">查看成员、指导教师和最近一次审核意见。</p>
            <p v-else>请选择一个团队查看详情。</p>
          </div>

          <div v-if="teamDetail" v-loading="detailLoading" class="team-detail">
            <div class="detail-grid">
              <div class="profile-item">
                <span>竞赛名称</span>
                <strong>{{ formatDisplayText(teamDetail.competitionTitle) }}</strong>
              </div>
              <div class="profile-item">
                <span>团队状态</span>
                <strong>{{ teamStatusLabel(teamDetail.teamStatus) }}</strong>
              </div>
              <div class="profile-item">
                <span>指导教师</span>
                <strong>{{ formatDisplayText(teamDetail.teacherName) || '--' }}</strong>
              </div>
              <div class="profile-item">
                <span>教师职称</span>
                <strong>{{ formatDisplayText(teamDetail.teacherTitle) || '--' }}</strong>
              </div>
            </div>

            <div class="inline-tip">
              <span>最近审核意见：</span>
              <strong>{{ formatDisplayText(teamDetail.latestAuditOpinion) || '暂无审核意见' }}</strong>
            </div>

            <div class="section-header section-header--compact">
              <h3>成员列表</h3>
              <el-button
                v-if="currentTeamEditable()"
                type="primary"
                plain
                size="small"
                @click="openMemberDialog"
              >
                添加成员
              </el-button>
            </div>

            <el-table :data="teamDetail.members" border size="small">
              <el-table-column label="姓名" min-width="100">
                <template #default="{ row }">{{ formatDisplayText(row.realName) }}</template>
              </el-table-column>
              <el-table-column prop="username" label="用户名" min-width="120" />
              <el-table-column label="学院" min-width="150">
                <template #default="{ row }">{{ formatDisplayText(row.collegeName) }}</template>
              </el-table-column>
              <el-table-column prop="memberRole" label="角色" width="90">
                <template #default="{ row }">
                  {{ row.memberRole === 'LEADER' ? '队长' : '成员' }}
                </template>
              </el-table-column>
              <el-table-column v-if="currentTeamEditable()" label="操作" width="90">
                <template #default="{ row }">
                  <el-button
                    v-if="row.memberRole !== 'LEADER'"
                    link
                    type="danger"
                    @click="handleRemoveMember(row)"
                  >
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <el-empty v-else description="尚未创建团队，可先创建团队后再维护成员和指导教师。" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '创建团队' : '编辑团队'"
      width="640px"
      destroy-on-close
    >
      <el-form :model="teamForm" label-width="90px">
        <el-form-item label="竞赛名称">
          <el-select
            v-model="teamForm.competitionId"
            :disabled="dialogMode === 'edit'"
            placeholder="请选择竞赛项目"
            style="width: 100%"
          >
            <el-option
              v-for="item in options.competitions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span>{{ item.label }}</span>
              <span style="float: right; color: #7a8b87">{{ item.extra }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="团队名称">
          <el-input v-model="teamForm.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="指导教师">
          <el-select v-model="teamForm.teacherId" placeholder="请选择指导教师" style="width: 100%">
            <el-option
              v-for="item in options.teachers"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span>{{ item.label }}</span>
              <span style="float: right; color: #7a8b87">{{ item.extra }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogMode === 'create'" label="初始成员">
          <el-select
            v-model="teamForm.memberUserIds"
            multiple
            collapse-tags
            collapse-tags-tooltip
            placeholder="可选择初始成员"
            style="width: 100%"
          >
            <el-option
              v-for="item in options.students"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span>{{ item.label }}</span>
              <span style="float: right; color: #7a8b87">{{ item.extra }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="teamForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitTeamForm">
          保存
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="memberDialogVisible" title="添加团队成员" width="460px">
      <el-form :model="memberForm" label-width="90px">
        <el-form-item label="学生成员">
          <el-select v-model="memberForm.userId" placeholder="请选择学生成员" style="width: 100%">
            <el-option
              v-for="item in options.students"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAddMember">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="submitDialogVisible" title="提交院级审核" width="480px">
      <el-form :model="submitForm" label-width="90px">
        <el-form-item label="提交说明">
          <el-input
            v-model="submitForm.remark"
            type="textarea"
            :rows="4"
            placeholder="可补充团队简介或申请说明。"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitForReview">
          确认提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
