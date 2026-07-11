<template>
  <AppLayout>
    <div class="dashboard">
      <div class="cards">
        <el-card class="card income"><div class="label">本月收入</div><div class="value">¥{{ stats.income }}</div></el-card>
        <el-card class="card expense"><div class="label">本月支出</div><div class="value">¥{{ stats.expense }}</div></el-card>
        <el-card class="card balance"><div class="label">本月结余</div><div class="value">¥{{ stats.balance }}</div></el-card>
      </div>

      <el-card class="insights" v-if="insights.length">
        <template #header>
          <div class="card-header">
            <span>AI 消费洞察</span>
            <el-button type="primary" size="small" :loading="insightLoading" @click="refreshInsights">重新生成</el-button>
          </div>
        </template>
        <ul>
          <li v-for="(item, i) in insights" :key="i">{{ item }}</li>
        </ul>
      </el-card>

      <el-card class="recent">
        <template #header>
          <div class="card-header">
            <span>最近账单</span>
            <el-button type="primary" size="small" @click="router.push('/bills')">新增</el-button>
          </div>
        </template>
        <el-table :data="recentBills" stripe size="small" v-loading="loading">
          <el-table-column prop="transactionDate" label="日期" width="110" />
          <el-table-column prop="categoryName" label="分类" width="100" />
          <el-table-column prop="description" label="描述" min-width="140" show-overflow-tooltip />
          <el-table-column label="金额" width="120" align="right">
            <template #default="{ row }">
              <span :class="row.type === 'INCOME' ? 'income-text' : 'expense-text'">
                {{ row.type === 'INCOME' ? '+' : '-' }}¥{{ row.amount }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../../components/AppLayout.vue'
import { listBills, type BillVO } from '../../api/bill'
import { getMonthlyStats } from '../../api/statistics'
import { getAnalysis } from '../../api/ai'
import { getToken } from '../../utils/auth'

const router = useRouter()
const loading = ref(false)
const recentBills = ref<BillVO[]>([])
const stats = ref({ income: 0, expense: 0, balance: 0 })
const insights = ref<string[]>([])
const insightLoading = ref(false)

onMounted(async () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1

  try {
    const res = await listBills({ page: 1, size: 8 })
    recentBills.value = res.data.records
  } catch { /* bill list failed, keep empty */ }

  try {
    const res = await getMonthlyStats(year, month)
    stats.value = res.data
  } catch { /* stats failed, keep zeros */ }

  // Default: load from cache
  loadInsights(year, month)
})

async function loadInsights(year: number, month: number) {
  try {
    const res = await getAnalysis({ year, month })
    insights.value = res.data.insights
  } catch { /* AI failed, non-blocking */ }
}

async function refreshInsights() {
  insightLoading.value = true
  insights.value = []
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  try {
    const token = getToken()
    const resp = await fetch('/api/ai/analysis/stream', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ year, month, forceRefresh: true })
    })
    const reader = resp.body?.getReader()
    if (!reader) throw new Error('No body')
    const decoder = new TextDecoder()
    let buffer = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('event:line') || line.startsWith('event:chunk')) continue
        if (line.startsWith('data:')) {
          const text = line.substring(5)
          if (text) {
            text.split('\n').filter(l => l.trim()).forEach(l => insights.value.push(l.trim()))
          }
        }
        if (line.startsWith('event:done')) { return }
      }
    }
  } catch { /* AI failed, non-blocking */ }
  finally { insightLoading.value = false }
}
</script>

<style scoped>
.cards { display: flex; gap: 16px; margin-bottom: 20px; }
.card { flex: 1; text-align: center; }
.card .label { color: #999; font-size: 14px; }
.card .value { font-size: 28px; font-weight: bold; margin-top: 8px; }
.card.income .value { color: #67c23a; }
.card.expense .value { color: #f56c6c; }
.card.balance .value { color: #409eff; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.income-text { color: #67c23a; }
.expense-text { color: #f56c6c; }
.insights { margin-bottom: 16px; }
.insights ul { margin: 0; padding-left: 20px; }
.insights li { margin-bottom: 6px; color: #606266; line-height: 1.6; }
</style>
