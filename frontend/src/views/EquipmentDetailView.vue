<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { borrow, getEquipment } from '@/api'
import type { Equipment } from '@/types'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)

const eq = ref<Equipment | null>(null)
const dueTime = ref('')
const purpose = ref('')
const error = ref('')
const msg = ref('')
const loading = ref(false)

const canBorrow = computed(() => eq.value?.status === 'AVAILABLE' && (eq.value?.availableQty ?? 0) > 0)

async function load() {
  try {
    eq.value = await getEquipment(id)
  } catch (e) {
    error.value = (e as Error).message
  }
}
onMounted(load)

function minDue() {
  const d = new Date(Date.now() + 3600000)
  return d.toISOString().slice(0, 16)
}

async function submit() {
  error.value = ''
  msg.value = ''
  loading.value = true
  try {
    await borrow(id, dueTime.value, purpose.value || undefined)
    msg.value = '借用成功！可到“我的借用”查看'
    load()
  } catch (e) {
    error.value = (e as Error).message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section v-if="eq">
    <button class="btn btn-ghost" @click="router.back()">← 返回</button>

    <div class="card">
      <h2>
        {{ eq.name }}
        <span class="badge" :class="canBorrow ? 'ok' : 'warn'">{{ canBorrow ? '可借' : '不可借' }}</span>
      </h2>
      <dl class="detail">
        <div><dt>编号</dt><dd>{{ eq.code }}</dd></div>
        <div><dt>类别</dt><dd>{{ eq.category || '-' }}</dd></div>
        <div><dt>实验室</dt><dd>{{ eq.labName || '-' }}</dd></div>
        <div><dt>可借 / 总数</dt><dd>{{ eq.availableQty }} / {{ eq.totalQty }}</dd></div>
        <div><dt>押金</dt><dd>{{ eq.deposit ? '¥' + eq.deposit : '-' }}</dd></div>
        <div><dt>状态</dt><dd>{{ eq.status === 'AVAILABLE' ? '可借' : '维护中' }}</dd></div>
        <div style="grid-column: 1 / -1"><dt>说明</dt><dd>{{ eq.description || '-' }}</dd></div>
      </dl>
    </div>

    <div class="card" v-if="canBorrow">
      <h3>申请借用</h3>
      <form @submit.prevent="submit" class="form">
        <label>应还时间</label>
        <input type="datetime-local" v-model="dueTime" :min="minDue()" required />
        <label>借用用途（选填）</label>
        <input v-model.trim="purpose" placeholder="例如：课程实验" />
        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="msg" class="msg">{{ msg }}</p>
        <button class="btn btn-primary" type="submit" :disabled="loading">
          {{ loading ? '提交中…' : '提交借用' }}
        </button>
      </form>
    </div>
  </section>
  <p v-else class="muted">{{ error || '加载中…' }}</p>
</template>
