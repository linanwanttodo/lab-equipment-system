<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { myLoans, returnLoan } from '@/api'
import type { Loan } from '@/types'
import Empty from '@/components/Empty.vue'

const list = ref<Loan[]>([])
const msg = ref('')
const error = ref('')
const loading = ref(false)

const borrowingCount = computed(() => list.value.filter((l) => l.status === 'BORROWING').length)
const overdueCount = computed(() => list.value.filter((l) => l.status === 'OVERDUE').length)
const returnedCount = computed(() => list.value.filter((l) => l.status === 'RETURNED').length)

async function load() {
  loading.value = true
  try {
    list.value = await myLoans()
  } catch (e) {
    error.value = (e as Error).message
  } finally {
    loading.value = false
  }
}
onMounted(load)

function statusText(s: string) {
  return s === 'BORROWING' ? '借用中' : s === 'RETURNED' ? '已归还' : '逾期'
}
function statusClass(s: string) {
  return s === 'RETURNED' ? 'ok' : s === 'OVERDUE' ? 'danger' : 'warn'
}
const canReturn = (l: Loan) => l.status !== 'RETURNED'

async function doReturn(l: Loan) {
  if (!confirm(`确认归还【${l.equipmentName}】？`)) return
  try {
    await returnLoan(l.id)
    msg.value = '归还成功'
    load()
  } catch (e) {
    error.value = (e as Error).message
  }
}

function fmt(t: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>我的借用</h2>
        <p class="subtitle muted">查看借用记录并归还设备</p>
      </div>
      <router-link to="/equipment" class="btn btn-primary" style="text-decoration: none">去借用设备</router-link>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-num">{{ list.length }}</div>
        <div class="stat-label">全部记录</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">{{ borrowingCount }}</div>
        <div class="stat-label">借用中</div>
      </div>
      <div class="stat-card" :class="{ alert: overdueCount > 0 }">
        <div class="stat-num">{{ overdueCount }}</div>
        <div class="stat-label">逾期未还</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">{{ returnedCount }}</div>
        <div class="stat-label">已归还</div>
      </div>
    </div>

    <p v-if="msg" class="msg">{{ msg }}</p>
    <p v-if="error" class="error">{{ error }}</p>
    <div v-if="loading" class="muted">加载中…</div>
    <table v-else class="table">
      <thead>
        <tr>
          <th>设备</th><th>编号</th><th>借出时间</th><th>应还时间</th>
          <th>归还时间</th><th>状态</th><th>逾期</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="l in list" :key="l.id">
          <td>{{ l.equipmentName }}</td>
          <td>{{ l.equipmentCode }}</td>
          <td>{{ fmt(l.borrowTime) }}</td>
          <td>{{ fmt(l.dueTime) }}</td>
          <td>{{ fmt(l.returnTime) }}</td>
          <td><span class="badge" :class="statusClass(l.status)">{{ statusText(l.status) }}</span></td>
          <td>{{ l.overdueDays > 0 ? l.overdueDays + ' 天' : '-' }}</td>
          <td>
            <button v-if="canReturn(l)" class="btn btn-small" @click="doReturn(l)">归还</button>
            <span v-else class="muted">-</span>
          </td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="8"><Empty text="暂无借用记录" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
