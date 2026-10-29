<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import UIcon from '@/components/UIcon.vue'

const auth = useAuthStore()
const router = useRouter()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(username.value, password.value)
    router.push(auth.isAdmin ? '/admin' : '/equipment')
  } catch (e) {
    error.value = (e as Error).message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-brand">
      <div class="logo-row">
        <div class="logo-mark" style="width: 38px; height: 38px">
          <UIcon name="flask" :size="20" />
        </div>
        <span class="logo-text" style="font-size: 18px">实验室设备借用系统</span>
      </div>
      <h1>校园实验室设备管理平台</h1>
      <p class="tagline">设备 · 借用 · 归还 · 逾期提醒，一站式管理</p>

      <div class="feature">
        <div class="f-ico"><UIcon name="box" :size="17" /></div>
        <div>
          <b>设备台账</b>
          <span>实验室、类别、库存一表管理，实时掌握可借数量</span>
        </div>
      </div>
      <div class="feature">
        <div class="f-ico"><UIcon name="clipboard" :size="17" /></div>
        <div>
          <b>借用归还</b>
          <span>学生在线申请借用、按时归还，库存自动扣减与恢复</span>
        </div>
      </div>
      <div class="feature">
        <div class="f-ico"><UIcon name="alarm" :size="17" /></div>
        <div>
          <b>逾期提醒</b>
          <span>定时扫描逾期记录，站内提醒加邮件通知双保险</span>
        </div>
      </div>

      <div class="auth-foot">5C 校园实验室设备借用系统</div>
    </div>

    <div class="auth-panel">
      <div class="auth-card">
        <h1>登录</h1>
        <p class="subtitle">使用系统账号登录工作台</p>
        <form @submit.prevent="submit">
          <label>用户名</label>
          <input v-model.trim="username" placeholder="请输入用户名" required />
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" required />
          <p v-if="error" class="error">{{ error }}</p>
          <button class="btn btn-primary" type="submit" :disabled="loading">
            {{ loading ? '登录中…' : '登 录' }}
          </button>
        </form>
        <p class="switch">还没有账号？<router-link to="/register">立即注册</router-link></p>
      </div>
    </div>
  </div>
</template>
