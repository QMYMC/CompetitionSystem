<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getStatisticsOverviewApi } from '@/api/statistics'
import { formatDisplayText } from '@/utils/display'

const loading = ref(false)
const overview = ref(null)

const registrationChartRef = ref(null)
const awardChartRef = ref(null)
const collegeChartRef = ref(null)

let registrationChart
let awardChart
let collegeChart

function initChart(instance, element) {
  if (!element) {
    return null
  }
  if (instance) {
    instance.dispose()
  }
  return echarts.init(element)
}

function renderCharts() {
  if (!overview.value) {
    return
  }

  registrationChart = initChart(registrationChart, registrationChartRef.value)
  awardChart = initChart(awardChart, awardChartRef.value)
  collegeChart = initChart(collegeChart, collegeChartRef.value)

  registrationChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['42%', '68%'],
        data: (overview.value.registrationTypeStats || []).map((item) => ({
          ...item,
          name: formatDisplayText(item.name),
        })),
      },
    ],
  })

  awardChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['42%', '68%'],
        data: (overview.value.awardStatusStats || []).map((item) => ({
          ...item,
          name: formatDisplayText(item.name),
        })),
      },
    ],
  })

  const collegeNames = (overview.value.collegeStats || []).map((item) => formatDisplayText(item.collegeName))
  collegeChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 40, right: 24, top: 48, bottom: 48 },
    xAxis: {
      type: 'category',
      data: collegeNames,
      axisLabel: { interval: 0, rotate: 20 },
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '报名数',
        type: 'bar',
        data: (overview.value.collegeStats || []).map((item) => item.registrationCount),
      },
      {
        name: '获奖数',
        type: 'bar',
        data: (overview.value.collegeStats || []).map((item) => item.awardCount),
      },
    ],
  })
}

function handleResize() {
  registrationChart?.resize()
  awardChart?.resize()
  collegeChart?.resize()
}

async function fetchOverview() {
  loading.value = true
  try {
    overview.value = await getStatisticsOverviewApi()
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOverview()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  registrationChart?.dispose()
  awardChart?.dispose()
  collegeChart?.dispose()
})
</script>

<template>
  <div class="management-page workflow-page">
    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>统计分析总览</h3>
        <p>统计数据涵盖竞赛数量、报名人数、获奖数量、公告数量，以及学院维度分析。</p>
      </div>

      <div v-if="overview" v-loading="loading" class="metric-grid">
        <el-card shadow="never" class="metric-card">
          <p class="metric-label">竞赛数量</p>
          <h3 class="metric-value">{{ overview.competitionCount }}</h3>
          <p class="metric-tip">当前系统已维护的竞赛总数。</p>
        </el-card>
        <el-card shadow="never" class="metric-card">
          <p class="metric-label">报名数量</p>
          <h3 class="metric-value">{{ overview.registrationCount }}</h3>
          <p class="metric-tip">包含个人赛与团队赛报名记录。</p>
        </el-card>
        <el-card shadow="never" class="metric-card">
          <p class="metric-label">获奖数量</p>
          <h3 class="metric-value">{{ overview.awardCount }}</h3>
          <p class="metric-tip">学生填报并进入系统的获奖记录总数。</p>
        </el-card>
        <el-card shadow="never" class="metric-card">
          <p class="metric-label">已发布公告</p>
          <h3 class="metric-value">{{ overview.publishedNoticeCount }}</h3>
          <p class="metric-tip">当前可面向师生查看的公告数量。</p>
        </el-card>
      </div>
    </el-card>

    <div class="chart-grid">
      <el-card class="panel-card chart-card" shadow="hover">
        <div class="section-header">
          <h3>报名类型分布</h3>
          <p>展示个人赛与团队赛在报名数据中的占比。</p>
        </div>
        <div ref="registrationChartRef" class="chart-box" />
      </el-card>

      <el-card class="panel-card chart-card" shadow="hover">
        <div class="section-header">
          <h3>获奖审核状态分布</h3>
          <p>展示待审核、通过、驳回等状态的数量分布。</p>
        </div>
        <div ref="awardChartRef" class="chart-box" />
      </el-card>
    </div>

    <el-card class="panel-card chart-card" shadow="hover">
      <div class="section-header">
        <h3>学院维度统计</h3>
        <p>按学院汇总报名数量与获奖数量，用于查看学院统计看板。</p>
      </div>
      <div ref="collegeChartRef" class="chart-box" />
    </el-card>

    <el-card class="panel-card" shadow="hover">
      <div class="section-header">
        <h3>学院明细表</h3>
        <p>表格展示报名总数、已通过报名数、获奖数和已通过获奖数。</p>
      </div>
      <el-table v-if="overview" :data="overview.collegeStats" border>
        <el-table-column label="学院名称" min-width="180">
          <template #default="{ row }">{{ formatDisplayText(row.collegeName) }}</template>
        </el-table-column>
        <el-table-column prop="registrationCount" label="报名总数" min-width="120" />
        <el-table-column prop="approvedRegistrationCount" label="通过报名数" min-width="120" />
        <el-table-column prop="awardCount" label="获奖总数" min-width="120" />
        <el-table-column prop="approvedAwardCount" label="通过获奖数" min-width="120" />
      </el-table>
    </el-card>
  </div>
</template>
