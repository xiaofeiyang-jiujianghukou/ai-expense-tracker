<template>
  <AppLayout>
    <div class="dashboard">
      <div class="cards">
        <el-card class="card income"><div class="label">本月收入</div><div class="value">¥{{ stats.income }}</div></el-card>
        <el-card class="card expense"><div class="label">本月支出</div><div class="value">¥{{ stats.expense }}</div></el-card>
        <el-card class="card balance"><div class="label">本月结余</div><div class="value">¥{{ stats.balance }}</div></el-card>
      </div>

      <el-card class="recent">
        <template #header>
          <div class="card-header">
            <span>最近账单</span>
            <el-button type="primary" size="small" @click="router.push('/transactions')">新增</el-button>
          </div>
        </template>
        <el-table :data="recentTxns" stripe size="small" v-loading="loading">
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
import { listTransactions, type TransactionVO } from '../../api/transaction'
import { getMonthlyStats } from '../../api/statistics'

const router = useRouter()
const loading = ref(false)
const recentTxns = ref<TransactionVO[]>([])
const stats = ref({ income: 0, expense: 0, balance: 0 })

onMounted(async () => {
  const now = new Date()
  try {
    const [txns, st] = await Promise.all([
      listTransactions({ page: 1, size: 8 }),
      getMonthlyStats(now.getFullYear(), now.getMonth() + 1)
    ])
    recentTxns.value = txns.data.records
    stats.value = st.data
  } catch { /* ignore */ }
})
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
</style>
