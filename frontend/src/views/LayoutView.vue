<script setup lang="ts">
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import UIcon from '@/components/UIcon.vue'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const collapsed = ref(false)
const userPopOpen = ref(false)
const userMenuEl = ref<HTMLElement | null>(null)

const isAdm = computed(() => auth.user?.role === 'ADMIN')
const displayName = computed(() => auth.user?.name || auth.user?.username || '')
const avatarChar = computed(() => displayName.value.slice(0, 1).toUpperCase())

interface MenuItem {
  to: string
  title: string
  icon: string
}

const studentMenus: MenuItem[] = [
  { to: '/equipment', title: '设备浏览', icon: 'box' },
  { to: '/my-loans', title: '我的借用', icon: 'clipboard' }
]

const adminMenus: MenuItem[] = [
  { to: '/admin', title: '后台总览', icon: 'dashboard' },
  { to: '/admin/equipment', title: '设备管理', icon: 'box' },
  { to: '/admin/labs', title: '实验室管理', icon: 'flask' },
  { to: '/admin/loans', title: '借用记录', icon: 'list' },
  { to: '/admin/overdue', title: '逾期提醒', icon: 'alarm' },
  { to: '/admin/users', title: '用户管理', icon: 'users' }
]

const menus = computed<MenuItem[]>(() => {
  if (isAdm.value) {
    // 管理员也能用学生功能：设备浏览 + 我的借用放在「功能」组
    return [...studentMenus, ...adminMenus]
  }
  return studentMenus
})

const currentTitle = computed(() => {
  const m = [...studentMenus, ...adminMenus].find((x) => x.to === route.path)
  return m?.title || (route.path.startsWith('/admin') ? '后台管理' : '页面')
})

function isActive(to: string) {
  if (to === '/admin') return route.path === '/admin'
  return route.path.startsWith(to)
}

function onDocClick(e: MouseEvent) {
  if (userMenuEl.value && !userMenuEl.value.contains(e.target as Node)) {
    userPopOpen.value = false
  }
}
onMounted(() => document.addEventListener('click', onDocClick))
onBeforeUnmount(() => document.removeEventListener('click', onDocClick))

function logout() {
  userPopOpen.value = false
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout" :class="{ collapsed }">
    <aside class="side">
      <div class="side-logo">
        <div class="logo-mark">
          <UIcon name="flask" :size="16" />
        </div>
        <span v-if="!collapsed" class="logo-text">实验室设备借用</span>
      </div>
      <nav class="side-menu">
        <div class="side-group">功能</div>
        <router-link
          v-for="m in menus.slice(0, 2)"
          :key="m.to"
          :to="m.to"
          class="side-item"
          :class="{ active: isActive(m.to) }"
        >
          <UIcon :name="m.icon" :size="16" />
          <span v-if="!collapsed">{{ m.title }}</span>
        </router-link>

        <template v-if="isAdm">
          <div class="side-group">管理</div>
          <router-link
            v-for="m in menus.slice(2)"
            :key="m.to"
            :to="m.to"
            class="side-item"
            :class="{ active: isActive(m.to) }"
          >
            <UIcon :name="m.icon" :size="16" />
            <span v-if="!collapsed">{{ m.title }}</span>
          </router-link>
        </template>
      </nav>
    </aside>

    <div class="main">
      <header class="topbar">
        <button class="icon-btn" @click="collapsed = !collapsed">
          <UIcon name="menu" :size="16" />
        </button>
        <div class="crumb">
          <span>{{ isAdm ? '管理后台' : '工作台' }}</span>
          <span class="sep">/</span>
          <span class="cur">{{ currentTitle }}</span>
        </div>
        <div class="spacer"></div>
        <div ref="userMenuEl" class="user-menu">
          <button class="user-btn" @click="userPopOpen = !userPopOpen">
            <span class="avatar">{{ avatarChar }}</span>
            <span>{{ displayName }}</span>
          </button>
          <div v-if="userPopOpen" class="user-pop">
            <div class="uinfo">
              <b>{{ displayName }}</b>
              <span>{{ auth.user?.username }} · {{ isAdm ? '管理员' : '学生' }}</span>
            </div>
            <router-link to="/my-loans" class="uitem" @click="userPopOpen = false">
              <UIcon name="clipboard" :size="14" />
              我的借用
            </router-link>
            <button class="uitem" @click="logout">
              <UIcon name="logout" :size="14" />
              退出登录
            </button>
          </div>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>
