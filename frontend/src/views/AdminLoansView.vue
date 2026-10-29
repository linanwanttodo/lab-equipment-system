<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { allLoans } from '@/api'
import type { Loan } from '@/types'
import Empty from '@/components/Empty.vue'

const list = ref<Loan[]>([])
const error = ref('')

async function load() {
  try {
    list.value = await allLoans()
  } catch (e) {
    error.value = (e as Error).message
  }
}
onMounted(load)

function statusText(s: string) {
  return s === 'BORROWING' ? '借用中' : s === 'RETURNED' ? '已归还' : '逾期'
}
function statusClass(s: string) {
  return s === 'RETURNED' ? 'ok' : s === 'OVERDUE' ? 'danger' : 'warn'
}
function fmt(t: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>全部借用记录</h2>
        <p class="subtitle muted">管理员查看全平台借用情况</p>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <table class="table">
      <thead>
        <tr><th>设备</th><th>借用人</th><th>借出</th><th>应还</th><th>归还</th><th>状态</th><th>逾期</th></tr>
      </thead>
      <tbody>
        <tr v-for="l in list" :key="l.id">
          <td>{{ l.equipmentName }} ({{ l.equipmentCode }})</td>
          <td>{{ l.userName }} ({{ l.username }})</td>
          <td>{{ fmt(l.borrowTime) }}</td>
          <td>{{ fmt(l.dueTime) }}</td>
          <td>{{ fmt(l.returnTime) }}</td>
          <td><span class="badge" :class="statusClass(l.status)">{{ statusText(l.status) }}</span></td>
          <td>{{ l.overdueDays > 0 ? l.overdueDays + ' 天' : '-' }}</td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="7"><Empty text="暂无借用记录" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
