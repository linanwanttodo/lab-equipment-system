<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { borrow, listEquipment, listLabs, listCategories } from '@/api'
import type { Equipment, Lab } from '@/types'
import Empty from '@/components/Empty.vue'

const list = ref<Equipment[]>([])
const labs = ref<Lab[]>([])
const categories = ref<string[]>([])
const keyword = ref('')
const labId = ref<number | undefined>(undefined)
const category = ref('')
const status = ref('')
const loading = ref(false)
const msg = ref('')

const canBorrow = (e: Equipment) => e.status === 'AVAILABLE' && e.availableQty > 0

async function load() {
  loading.value = true
  msg.value = ''
  try {
    list.value = await listEquipment({
      keyword: keyword.value || undefined,
      labId: labId.value,
      category: category.value || undefined,
      status: status.value || undefined
    })
  } catch (e) {
    msg.value = (e as Error).message
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  load()
  try {
    labs.value = await listLabs()
    categories.value = await listCategories()
  } catch {
    labs.value = []
  }
})

function pad(n: number) {
  return n < 10 ? '0' + n : '' + n
}

function quickBorrow(e: Equipment) {
  if (!confirm(`借用【${e.name}】，默认 7 天后归还？`)) return
  const d = new Date(Date.now() + 7 * 86400000)
  const dueTime = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
  borrow(e.id, dueTime, '网页快速借用')
    .then(() => {
      msg.value = `已借用【${e.name}】，请在“我的借用”中查看`
      load()
    })
    .catch((err) => {
      msg.value = (err as Error).message
    })
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>设备列表</h2>
        <p class="subtitle muted">浏览并借用实验室设备</p>
      </div>
    </div>

    <div class="filters">
      <input v-model.trim="keyword" placeholder="搜索名称 / 编号" @keyup.enter="load" />
      <select v-model="labId">
        <option :value="undefined">全部实验室</option>
        <option v-for="l in labs" :key="l.id" :value="l.id">{{ l.name }}</option>
      </select>
      <select v-model="category">
        <option value="">全部类别</option>
        <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
      </select>
      <select v-model="status">
        <option value="">全部状态</option>
        <option value="AVAILABLE">可借</option>
        <option value="MAINTENANCE">维护中</option>
      </select>
      <button class="btn btn-primary" @click="load">查询</button>
    </div>

    <p v-if="msg" class="msg">{{ msg }}</p>
    <div v-if="loading" class="muted">加载中…</div>
    <table v-else class="table">
      <thead>
        <tr>
          <th>编号</th><th>名称</th><th>类别</th><th>实验室</th>
          <th>可借 / 总数</th><th>押金</th><th>状态</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="e in list" :key="e.id">
          <td>{{ e.code }}</td>
          <td><router-link :to="'/equipment/' + e.id">{{ e.name }}</router-link></td>
          <td>{{ e.category || '-' }}</td>
          <td>{{ e.labName || '-' }}</td>
          <td>{{ e.availableQty }} / {{ e.totalQty }}</td>
          <td>{{ e.deposit ? '¥' + e.deposit : '-' }}</td>
          <td>
            <span class="badge" :class="e.status === 'AVAILABLE' ? 'ok' : 'warn'">
              {{ e.status === 'AVAILABLE' ? '可借' : '维护中' }}
            </span>
          </td>
          <td>
            <button v-if="canBorrow(e)" class="btn btn-primary btn-small" @click="quickBorrow(e)">借用</button>
            <span v-else class="muted">不可借</span>
          </td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="8"><Empty text="暂无设备" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
