import request from './request'

export interface TransactionVO {
  id: number
  categoryId: number
  categoryName: string
  amount: number
  type: string
  description: string
  transactionDate: string
  createdTime: string
}

export interface TransactionListRequest {
  type?: string
  categoryId?: number
  startDate?: string
  endDate?: string
  page?: number
  size?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export function listTransactions(params: TransactionListRequest) {
  return request.post<any, { data: PageResult<TransactionVO> }>('/transactions/list', params)
}

export function createTransaction(data: {
  categoryId: number
  amount: number
  type: string
  description?: string
  transactionDate: string
}) {
  return request.post('/transactions', data)
}

export function updateTransaction(data: {
  id: number
  categoryId: number
  amount: number
  type: string
  description?: string
  transactionDate: string
}) {
  return request.post('/transactions/update', data)
}

export function deleteTransaction(id: number) {
  return request.post('/transactions/delete', { id })
}
