<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createLab, deleteLab, listLabs, updateLab } from '@/api'
import type { Lab } from '@/types'
import Empty from '@/components/Empty.vue'

const list = ref<Lab[]>([])
const error = ref('')
const msg = ref('')
const editing = ref<Lab | null>(null)
const formOpen = ref(false)
const form = ref({ name: '', location: '' })

async function load() {
  error.value = ''
  try {
    list.value = await listLabs()
  } catch (e) {
    error.value = (e as Error).message
  }
}
onMounted(load)

function openCreate() {
  editing.value = null
  form.value = { name: '', location: '' }
  formOpen.value = true
}

function openEdit(l: Lab) {
  editing.value = l
  form.value = { name: l.name, location: l.location || '' }
  formOpen.value = true
}

async function save() {
  error.value = ''
  msg.value = ''
  try {
    if (editing.value) {
      await updateLab(editing.value.id, form.value)
      msg.value = '已更新'
    } else {
      await createLab(form.value)
      msg.value = '已新增'
    }
    formOpen.value = false
    load()
  } catch (e) {
    error.value = (e as Error).message
  }
}

async function del(l: Lab) {
  if (!confirm(`删除【${l.name}】？`)) return
  try {
    await deleteLab(l.id)
    msg.value = '已删除'
    load()
  } catch (e) {
    error.value = (e as Error).message
  }
}
</script>

<template>
  <section>
    <div class="page-head">
      <div>
        <h2>实验室管理</h2>
        <p class="subtitle muted">维护实验室信息，删除前需先清空其设备</p>
      </div>
      <button class="btn btn-primary" @click="openCreate">+ 新增实验室</button>
    </div>

    <p v-if="msg" class="msg">{{ msg }}</p>
    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="formOpen" class="card">
      <h3>{{ editing ? '编辑实验室' : '新增实验室' }}</h3>
      <form @submit.prevent="save" class="form">
        <div class="grid2">
          <label>名称</label>
          <input v-model.trim="form.name" required />
          <label>位置</label>
          <input v-model.trim="form.location" placeholder="例如：A 栋 301" />
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" type="submit">保存</button>
          <button class="btn btn-ghost" type="button" @click="formOpen = false">取消</button>
        </div>
      </form>
    </div>

    <table class="table">
      <thead>
        <tr><th>ID</th><th>名称</th><th>位置</th><th>设备数</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="l in list" :key="l.id">
          <td>{{ l.id }}</td>
          <td>{{ l.name }}</td>
          <td>{{ l.location || '-' }}</td>
          <td>{{ l.equipmentCount }}</td>
          <td>
            <button class="btn btn-small" @click="openEdit(l)">编辑</button>
            <button class="btn btn-small btn-danger" @click="del(l)">删除</button>
          </td>
        </tr>
        <tr v-if="list.length === 0">
          <td colspan="5"><Empty text="暂无实验室" /></td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
