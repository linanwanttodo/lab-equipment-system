import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import LayoutView from '@/views/LayoutView.vue'
import EquipmentView from '@/views/EquipmentView.vue'
import EquipmentDetailView from '@/views/EquipmentDetailView.vue'
import MyLoansView from '@/views/MyLoansView.vue'
import AdminEquipmentView from '@/views/AdminEquipmentView.vue'
import AdminLoansView from '@/views/AdminLoansView.vue'
import AdminOverdueView from '@/views/AdminOverdueView.vue'
import DashboardView from '@/views/DashboardView.vue'
import AdminLabsView from '@/views/AdminLabsView.vue'
import AdminUsersView from '@/views/AdminUsersView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView, meta: { public: true, title: '登录' } },
    { path: '/register', name: 'register', component: RegisterView, meta: { public: true, title: '注册' } },
    {
      path: '/',
      component: LayoutView,
      children: [
        { path: '', redirect: '/equipment' },
        { path: 'equipment', name: 'equipment', component: EquipmentView, meta: { title: '设备浏览' } },
        { path: 'equipment/:id', name: 'equipment-detail', component: EquipmentDetailView, meta: { title: '设备详情' } },
        { path: 'my-loans', name: 'my-loans', component: MyLoansView, meta: { title: '我的借用' } },
        { path: 'admin', name: 'dashboard', component: DashboardView, meta: { admin: true, title: '后台总览' } },
        { path: 'admin/equipment', name: 'admin-equipment', component: AdminEquipmentView, meta: { admin: true, title: '设备管理' } },
        { path: 'admin/labs', name: 'admin-labs', component: AdminLabsView, meta: { admin: true, title: '实验室管理' } },
        { path: 'admin/loans', name: 'admin-loans', component: AdminLoansView, meta: { admin: true, title: '借用记录' } },
        { path: 'admin/overdue', name: 'admin-overdue', component: AdminOverdueView, meta: { admin: true, title: '逾期提醒' } },
        { path: 'admin/users', name: 'admin-users', component: AdminUsersView, meta: { admin: true, title: '用户管理' } }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/equipment' }
  ]
})

router.afterEach((to) => {
  document.title = to.meta.title
    ? `${to.meta.title} · 校园实验室设备借用系统`
    : '校园实验室设备借用系统'
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (to.meta.public) {
    if (auth.isLoggedIn && to.name === 'login') return { name: 'equipment' }
    return true
  }
  if (!auth.isLoggedIn) return { name: 'login' }
  if (!auth.user) await auth.loadMe()
  if (to.meta.admin && auth.user?.role !== 'ADMIN') return { name: 'equipment' }
  return true
})

export default router
