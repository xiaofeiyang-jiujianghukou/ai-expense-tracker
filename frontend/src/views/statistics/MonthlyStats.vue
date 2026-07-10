<template>
  <AppLayout>
    <div class="stats-page">
      <div class="toolbar">
        <el-date-picker v-model="selectedMonth" type="month" format="YYYY年M月" value-format="YYYY-MM"
          placeholder="选择月份" @change="fetchStats" />
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
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import AppLayout from '../../components/AppLayout.vue'
import { getMonthlyStats } from '../../api/statistics'

const now = new Date()
const selectedMonth = ref(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`)
const stats = reactive({ income: 0, expense: 0, balance: 0, categoryBreakdown: [] as any[] })

onMounted(() => fetchStats())
async function fetchStats() {
  const [y, m] = selectedMonth.value.split('-')
  try {
    const res = await getMonthlyStats(Number(y), Number(m))
    Object.assign(stats, res.data)
  } catch { /* ignore */ }
}
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.cards { display: flex; gap: 16px; margin-bottom: 20px; }
.card { flex: 1; text-align: center; }
.card .label { color: #999; font-size: 14px; }
.card .value { font-size: 28px; font-weight: bold; margin-top: 8px; }
.card.income .value { color: #67c23a; }
.card.expense .value { color: #f56c6c; }
.card.balance .value { color: #409eff; }
.pct-bar { display: flex; align-items: center; gap: 8px; }
.pct-fill { height: 8px; background: #409eff; border-radius: 4px; min-width: 2px; }
</style>
