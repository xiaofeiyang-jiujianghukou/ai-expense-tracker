<template>
  <AppLayout>
    <div class="stats-page">
      <div class="toolbar">
        <el-date-picker v-model="selectedMonth" type="month" format="YYYY年M月" value-format="YYYY-MM"
          placeholder="选择月份" @change="fetchStats" />
        <el-button type="primary" :loading="reportLoading" @click="fetchReport">生成 AI 报告</el-button>
      </div>

      <div class="cards">
        <el-card class="card income"><div class="label">收入</div><div class="value">¥{{ stats.income }}</div></el-card>
        <el-card class="card expense"><div class="label">支出</div><div class="value">¥{{ stats.expense }}</div></el-card>
        <el-card class="card balance"><div class="label">结余</div><div class="value">¥{{ stats.balance }}</div></el-card>
      </div>

      <el-card v-if="stats.categoryBreakdown?.length" class="breakdown">
        <template #header><span>支出分类占比</span></template>
        <el-table :data="stats.categoryBreakdown" stripe size="small">
          <el-table-column prop="categoryName" label="分类" />
          <el-table-column prop="amount" label="金额" align="right" />
          <el-table-column label="占比" width="180">
            <template #default="{ row }">
              <div class="pct-bar">
                <div class="pct-fill" :style="{ width: row.percentage + '%' }"></div>
                <span>{{ row.percentage }}%</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card v-if="report" class="report-card">
        <template #header><span>AI 财务报告</span></template>
        <div class="report-content">{{ report }}</div>
      </el-card>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import AppLayout from '../../components/AppLayout.vue'
import { getMonthlyStats } from '../../api/statistics'
import { getReport } from '../../api/ai'
import { getToken } from '../../utils/auth'

const now = new Date()
const selectedMonth = ref(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`)
const stats = reactive({ income: 0, expense: 0, balance: 0, categoryBreakdown: [] as any[] })
const report = ref('')
const reportLoading = ref(false)

onMounted(() => { fetchStats(); loadCachedReport() })
async function fetchStats() {
  const [y, m] = selectedMonth.value.split('-')
  try {
    const res = await getMonthlyStats(Number(y), Number(m))
    Object.assign(stats, res.data)
  } catch { /* ignore */ }
}

// Load cached report on page enter (no force refresh)
async function loadCachedReport() {
  const [y, m] = selectedMonth.value.split('-')
  try {
    const res = await getReport({ year: Number(y), month: Number(m) })
    report.value = res.data.report
  } catch { /* no cache or error, wait for user to click generate */ }
}

// Force regenerate via SSE streaming
async function fetchReport() {
  reportLoading.value = true
  report.value = ''
  const [y, m] = selectedMonth.value.split('-')
  try {
    const token = getToken()
    const resp = await fetch('/api/ai/report/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ year: Number(y), month: Number(m), forceRefresh: true })
    })
    const reader = resp.body?.getReader()
    if (!reader) throw new Error('No response body')
    const decoder = new TextDecoder()
    let buffer = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('event:chunk')) continue
        if (line.startsWith('data:')) {
          report.value += line.substring(5)
        }
        if (line.startsWith('event:done')) {
          reportLoading.value = false
          return
        }
      }
    }
  } catch { /* AI failed, non-blocking */ }
  finally { reportLoading.value = false }
}
</script>

<style scoped>
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; align-items: center; }
.cards { display: flex; gap: 16px; margin-bottom: 20px; }
.card { flex: 1; text-align: center; }
.card .label { color: #999; font-size: 14px; }
.card .value { font-size: 28px; font-weight: bold; margin-top: 8px; }
.card.income .value { color: #67c23a; }
.card.expense .value { color: #f56c6c; }
.card.balance .value { color: #409eff; }
.pct-bar { display: flex; align-items: center; gap: 8px; }
.pct-fill { height: 8px; background: #409eff; border-radius: 4px; min-width: 2px; }
.report-card { margin-top: 20px; }
.report-content { white-space: pre-wrap; line-height: 1.8; color: #606266; }
</style>
