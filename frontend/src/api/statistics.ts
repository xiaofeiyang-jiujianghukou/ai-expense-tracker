import request from './request'

export interface MonthlyStatsVO {
  year: number
  month: number
  income: number
  expense: number
  balance: number
  categoryBreakdown: Array<{
    categoryId: number
    categoryName: string
    amount: number
    percentage: number
  }>
}

export function getMonthlyStats(year: number, month: number) {
  return request.post<any, { data: MonthlyStatsVO }>('/statistics/monthly', { year, month })
}
