<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listUsers } from '@/api'
import type { UserResponse } from '@/types'
import Empty from '@/components/Empty.vue'

const list = ref<UserResponse[]>([])
const error = ref('')

onMounted(async () => {
  try {
    list.value = await listUsers()
  } catch (e) {
    error.value = (e as Error).message
  }
})

function fmt(t: string | null) {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>用户管理</h2>
        <p class="subtitle muted">查看注册用户（只读）</p>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <table class="table">
      <thead>
        <tr><th>ID</th><th>用户名</th><th>姓名</th><th>联系方式</th><th>角色</th><th>注册时间</th></tr>
      </thead>
      <tbody>
        <tr v-for="u in list" :key="u.id">
          <td>{{ u.id }}</td>
          <td>{{ u.username }}</td>
          <td>{{ u.name }}</td>
          <td>{{ u.contact || '-' }}</td>
          <td>
            <span class="badge" :class="u.role === 'ADMIN' ? 'warn' : 'ok'">
              {{ u.role === 'ADMIN' ? '管理员' : '学生' }}
            </span>
          </td>
          <td>{{ fmt(u.createdAt) }}</td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="6"><Empty text="暂无用户" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
