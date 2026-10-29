<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { overdueLoans } from '@/api'
import type { Loan } from '@/types'
import Empty from '@/components/Empty.vue'

const list = ref<Loan[]>([])
const error = ref('')
const count = computed(() => list.value.length)

async function load() {
  try {
    list.value = await overdueLoans()
  } catch (e) {
    error.value = (e as Error).message
  }
}
onMounted(load)

function fmt(t: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>逾期提醒</h2>
        <p class="subtitle muted">
          系统每 60 秒自动扫描，将逾期记录标记并发送邮件提醒（需在 application.yml 配置 SMTP 并开启 app.mail.enabled）
        </p>
      </div>
      <span class="badge" :class="count > 0 ? 'danger' : 'ok'">{{ count > 0 ? count + ' 条逾期' : '无逾期' }}</span>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <table class="table">
      <thead>
        <tr><th>设备</th><th>借用人</th><th>应还时间</th><th>逾期天数</th></tr>
      </thead>
      <tbody>
        <tr v-for="l in list" :key="l.id">
          <td>{{ l.equipmentName }} ({{ l.equipmentCode }})</td>
          <td>{{ l.userName }} ({{ l.username }})</td>
          <td>{{ fmt(l.dueTime) }}</td>
          <td><span class="badge danger">{{ l.overdueDays }} 天</span></td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="4"><Empty text="当前没有逾期记录" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
