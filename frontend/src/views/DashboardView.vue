<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getDashboard } from '@/api'
import type { DashboardStats } from '@/types'
import UIcon from '@/components/UIcon.vue'
import Empty from '@/components/Empty.vue'

const stats = ref<DashboardStats | null>(null)
const error = ref('')

onMounted(async () => {
  try {
    stats.value = await getDashboard()
  } catch (e) {
    error.value = (e as Error).message
  }
})

interface StatItem {
  label: string
  value: number
  icon: string
  color: string
  alert?: boolean
}

function buildItems(s: DashboardStats): StatItem[] {
  return [
    { label: '用户数', value: s.userCount, icon: 'users', color: 'i-blue' },
    { label: '实验室', value: s.labCount, icon: 'flask', color: 'i-cyan' },
    { label: '设备种类', value: s.equipmentCount, icon: 'box', color: 'i-purple' },
    { label: '可借库存', value: s.equipmentAvailable, icon: 'check', color: 'i-green' },
    { label: '借用总数', value: s.loanTotal, icon: 'clipboard', color: 'i-blue' },
    { label: '借用中', value: s.loanBorrowing, icon: 'clock', color: 'i-orange' },
    { label: '逾期未还', value: s.loanOverdue, icon: 'alert', color: 'i-red', alert: s.loanOverdue > 0 },
    { label: '已归还', value: s.loanReturned, icon: 'check', color: 'i-green' }
  ]
}

function statusText(s: string) {
  return s === 'BORROWING' ? '借用中' : s === 'RETURNED' ? '已归还' : '逾期'
}
function statusClass(s: string) {
  return s === 'RETURNED' ? 'ok' : s === 'OVERDUE' ? 'danger' : 'warn'
}
function fmt(t: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}
function maxCount(list: { count: number }[]) {
  return Math.max(1, ...list.map((x) => x.count))
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>后台总览</h2>
        <p class="subtitle">系统运行数据一览</p>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <template v-if="stats">
      <div class="stats-grid">
        <div v-for="it in buildItems(stats)" :key="it.label" class="stat-card" :class="{ alert: it.alert }">
          <div class="stat-icon" :class="it.color"><UIcon :name="it.icon" :size="20" /></div>
          <div>
            <div class="stat-num">{{ it.value }}</div>
            <div class="stat-label">{{ it.label }}</div>
          </div>
        </div>
      </div>

      <div class="quick">
        <router-link to="/admin/equipment"><UIcon name="plus" :size="14" /> 新增设备</router-link>
        <router-link to="/admin/labs"><UIcon name="flask" :size="14" /> 实验室管理</router-link>
        <router-link to="/admin/loans"><UIcon name="list" :size="14" /> 借用记录</router-link>
        <router-link to="/admin/overdue"><UIcon name="alarm" :size="14" /> 逾期提醒</router-link>
        <router-link to="/admin/users"><UIcon name="users" :size="14" /> 用户管理</router-link>
      </div>

      <div class="dash-cols">
        <div class="card">
          <div class="card-title-row">
            <h3>设备类别分布</h3>
            <router-link to="/admin/equipment">查看全部</router-link>
          </div>
          <table class="table plain" v-if="stats.categoryStats.length">
            <tbody>
              <tr v-for="c in stats.categoryStats" :key="c.name">
                <td class="w-label">{{ c.name }}</td>
                <td>
                  <div class="bar-track">
                    <div class="bar" :style="{ width: (c.count / maxCount(stats.categoryStats) * 100) + '%' }"></div>
                  </div>
                </td>
                <td class="w-num">{{ c.count }}</td>
              </tr>
            </tbody>
          </table>
          <Empty v-else />
        </div>

        <div class="card">
          <div class="card-title-row">
            <h3>实验室设备分布</h3>
            <router-link to="/admin/labs">查看全部</router-link>
          </div>
          <table class="table plain" v-if="stats.labStats.length">
            <tbody>
              <tr v-for="l in stats.labStats" :key="l.labId ?? l.labName">
                <td class="w-label">{{ l.labName }}</td>
                <td>
                  <div class="bar-track">
                    <div class="bar alt" :style="{ width: (l.count / maxCount(stats.labStats) * 100) + '%' }"></div>
                  </div>
                </td>
                <td class="w-num">{{ l.count }}</td>
              </tr>
            </tbody>
          </table>
          <Empty v-else />
        </div>
      </div>

      <div class="card">
        <div class="card-title-row">
          <h3>逾期 TOP（按逾期天数）</h3>
          <router-link to="/admin/overdue">查看全部</router-link>
        </div>
        <table class="table" v-if="stats.overdueTop.length">
          <thead>
            <tr><th>设备</th><th>借用人</th><th>应还时间</th><th>逾期天数</th></tr>
          </thead>
          <tbody>
            <tr v-for="l in stats.overdueTop" :key="l.id">
              <td>{{ l.equipmentName }} ({{ l.equipmentCode }})</td>
              <td>{{ l.userName }}</td>
              <td>{{ fmt(l.dueTime) }}</td>
              <td><span class="badge danger">{{ l.overdueDays }} 天</span></td>
            </tr>
          </tbody>
        </table>
        <Empty v-else text="当前没有逾期记录" />
      </div>

      <div class="card">
        <div class="card-title-row">
          <h3>最近借用</h3>
          <router-link to="/admin/loans">查看全部</router-link>
        </div>
        <table class="table" v-if="stats.recentLoans.length">
          <thead>
            <tr><th>设备</th><th>借用人</th><th>借出时间</th><th>应还时间</th><th>状态</th></tr>
          </thead>
          <tbody>
            <tr v-for="l in stats.recentLoans" :key="l.id">
              <td>{{ l.equipmentName }} ({{ l.equipmentCode }})</td>
              <td>{{ l.userName }}</td>
              <td>{{ fmt(l.borrowTime) }}</td>
              <td>{{ fmt(l.dueTime) }}</td>
              <td><span class="badge" :class="statusClass(l.status)">{{ statusText(l.status) }}</span></td>
            </tr>
          </tbody>
        </table>
        <Empty v-else text="还没有借用记录" />
      </div>
    </template>
    <p v-else-if="!error" class="muted">加载中…</p>
  </section>
</template>
