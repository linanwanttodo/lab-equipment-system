import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getMe, login as apiLogin, register as apiRegister } from '@/api'

export interface AuthUser {
  id: number
  username: string
  name: string
  role: 'STUDENT' | 'ADMIN'
}

const TOKEN_KEY = 'token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<AuthUser | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  function setToken(t: string) {
    token.value = t
    if (t) localStorage.setItem(TOKEN_KEY, t)
    else localStorage.removeItem(TOKEN_KEY)
  }

  async function login(username: string, password: string) {
    const res = await apiLogin(username, password)
    setToken(res.token)
    user.value = { id: res.id, username: res.username, name: res.name, role: res.role }
  }

  async function register(username: string, password: string, name: string, contact?: string) {
    const res = await apiRegister({ username, password, name, contact })
    setToken(res.token)
    user.value = { id: res.id, username: res.username, name: res.name, role: res.role }
  }

  async function loadMe() {
    if (!token.value) return
    try {
      user.value = await getMe()
    } catch {
      logout()
    }
  }

  function logout() {
    setToken('')
    user.value = null
  }

  return { token, user, isLoggedIn, isAdmin, login, register, loadMe, logout, setToken }
})
