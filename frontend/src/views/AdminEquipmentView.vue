<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  createEquipment,
  deleteEquipment,
  listEquipment,
  listLabs,
  updateEquipment
} from '@/api'
import type { Equipment, Lab } from '@/types'
import Empty from '@/components/Empty.vue'

interface EquipmentForm {
  code: string
  name: string
  category: string
  labId: number | undefined
  totalQty: number
  deposit: number
  status: 'AVAILABLE' | 'MAINTENANCE'
  description: string
}

const emptyForm: EquipmentForm = {
  code: '', name: '', category: '', labId: undefined, totalQty: 1, deposit: 0, status: 'AVAILABLE', description: ''
}

const list = ref<Equipment[]>([])
const labs = ref<Lab[]>([])
const error = ref('')
const msg = ref('')
const editing = ref<Equipment | null>(null)
const formOpen = ref(false)
const form = ref<EquipmentForm>({ ...emptyForm })

async function load() {
  error.value = ''
  try {
    list.value = await listEquipment({})
    labs.value = await listLabs()
  } catch (e) {
    error.value = (e as Error).message
  }
}
onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { ...emptyForm }
  formOpen.value = true
}

function openEdit(e: Equipment) {
  editing.value = e
  form.value = {
    code: e.code,
    name: e.name,
    category: e.category || '',
    labId: e.labId ?? undefined,
    totalQty: e.totalQty,
    deposit: e.deposit || 0,
    status: e.status,
    description: e.description || ''
  }
  formOpen.value = true
}

async function save() {
  error.value = ''
  msg.value = ''
  try {
    if (editing.value) {
      await updateEquipment(editing.value.id, form.value)
      msg.value = '已更新'
    } else {
      await createEquipment(form.value)
      msg.value = '已新增'
    }
    formOpen.value = false
    load()
  } catch (e) {
    error.value = (e as Error).message
  }
}

async function del(e: Equipment) {
  if (!confirm(`删除【${e.name}】？`)) return
  try {
    await deleteEquipment(e.id)
    msg.value = '已删除'
    load()
  } catch (err) {
    error.value = (err as Error).message
  }
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>设备管理</h2>
        <p class="subtitle muted">新增 / 编辑 / 删除设备与库存</p>
      </div>
      <button class="btn btn-primary" @click="openCreate">+ 新增设备</button>
    </div>

    <p v-if="msg" class="msg">{{ msg }}</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="formOpen" class="card">
      <h3>{{ editing ? '编辑设备' : '新增设备' }}</h3>
      <form @submit.prevent="save" class="form">
        <div class="grid2">
          <label>编号</label>
          <input v-model.trim="form.code" required />
          <label>名称</label>
          <input v-model.trim="form.name" required />
          <label>类别</label>
          <input v-model.trim="form.category" />
          <label>实验室</label>
          <select v-model="form.labId">
            <option :value="undefined">无</option>
            <option v-for="l in labs" :key="l.id" :value="l.id">{{ l.name }}</option>
          </select>
          <label>总数</label>
          <input type="number" min="1" v-model.number="form.totalQty" required />
          <label>押金</label>
          <input type="number" step="0.01" min="0" v-model.number="form.deposit" />
          <label>状态</label>
          <select v-model="form.status">
            <option value="AVAILABLE">可借</option>
            <option value="MAINTENANCE">维护中</option>
          </select>
          <label>说明</label>
          <input v-model.trim="form.description" />
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" type="submit">保存</button>
          <button class="btn btn-ghost" type="button" @click="formOpen = false">取消</button>
        </div>
      </form>
    </div>

    <table class="table">
      <thead>
        <tr><th>编号</th><th>名称</th><th>实验室</th><th>可借 / 总数</th><th>状态</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="e in list" :key="e.id">
          <td>{{ e.code }}</td>
          <td>{{ e.name }}</td>
          <td>{{ e.labName || '-' }}</td>
          <td>{{ e.availableQty }} / {{ e.totalQty }}</td>
          <td>
            <span class="badge" :class="e.status === 'AVAILABLE' ? 'ok' : 'warn'">
              {{ e.status === 'AVAILABLE' ? '可借' : '维护中' }}
            </span>
          </td>
          <td>
            <button class="btn btn-small" @click="openEdit(e)">编辑</button>
            <button class="btn btn-small btn-danger" @click="del(e)">删除</button>
          </td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="6"><Empty text="暂无设备" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
