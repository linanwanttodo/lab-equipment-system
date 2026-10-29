<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import UIcon from '@/components/UIcon.vue'

const auth = useAuthStore()
const router = useRouter()
const form = ref({ username: '', password: '', name: '', contact: '' })
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.register(form.value.username, form.value.password, form.value.name, form.value.contact || undefined)
    router.push('/equipment')
  } catch (e) {
    error.value = (e as Error).message || '注册失败'
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
      <h1>创建你的账号</h1>
      <p class="tagline">注册后即可在线浏览与借用实验室设备</p>

      <div class="feature">
        <div class="f-ico"><UIcon name="user" :size="17" /></div>
        <div>
          <b>学生角色</b>
          <span>注册用户默认为学生，可自助借用与归还设备</span>
        </div>
      </div>
      <div class="feature">
        <div class="f-ico"><UIcon name="calendar" :size="17" /></div>
        <div>
          <b>借用记录</b>
          <span>借出、应还、归还全程留痕，随时查看进度</span>
        </div>
      </div>

      <div class="auth-foot">5C 校园实验室设备借用系统</div>
    </div>

    <div class="auth-panel">
      <div class="auth-card">
        <h1>注册</h1>
        <p class="subtitle">注册后默认为学生角色</p>
        <form @submit.prevent="submit">
          <label>用户名</label>
          <input v-model.trim="form.username" placeholder="3-50 位字符" required />
          <label>姓名</label>
          <input v-model.trim="form.name" placeholder="真实姓名" required />
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="至少 6 位" required />
          <label>邮箱（用于逾期提醒，选填）</label>
          <input v-model.trim="form.contact" placeholder="you@example.com" />
          <p v-if="error" class="error">{{ error }}</p>
          <button class="btn btn-primary" type="submit" :disabled="loading">
            {{ loading ? '注册中…' : '注 册' }}
          </button>
        </form>
        <p class="switch">已有账号？<router-link to="/login">直接登录</router-link></p>
      </div>
    </div>
  </div>
</template>
