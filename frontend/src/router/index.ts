import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '../utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/register/RegisterView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/dashboard/DashboardView.vue')
    },
    {
      path: '/bills',
      name: 'bills',
      component: () => import('../views/bills/BillList.vue')
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('../views/categories/CategoryManage.vue')
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('../views/statistics/MonthlyStats.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: to => {
        // 未登录 → /login，已登录 → /dashboard
        return isLoggedIn() ? '/dashboard' : '/login'
      }
    }
  ]
})

const publicPaths = ['/login', '/register']

router.beforeEach((to, _from, next) => {
  if (isLoggedIn()) {
    // 已登录：访问登录/注册页 → 跳转仪表盘
    if (publicPaths.includes(to.path)) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    // 未登录：允许访问登录/注册页，其他一律跳转登录
    if (publicPaths.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
