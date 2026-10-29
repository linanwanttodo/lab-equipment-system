import http from './http'
import type { DashboardStats, Equipment, Lab, Loan, UserResponse } from '@/types'

export interface AuthResult {
  token: string
  id: number
  username: string
  name: string
  role: 'STUDENT' | 'ADMIN'
}

export function login(username: string, password: string): Promise<AuthResult> {
  return http.post('/auth/login', { username, password }).then((r) => r.data)
}

export function register(payload: {
  username: string
  password: string
  name: string
  contact?: string
}): Promise<AuthResult> {
  return http.post('/auth/register', payload).then((r) => r.data)
}

export function getMe(): Promise<UserResponse> {
  return http.get('/auth/me').then((r) => r.data)
}

export function listEquipment(params: {
  labId?: number
  category?: string
  status?: string
  keyword?: string
}): Promise<Equipment[]> {
  return http.get('/equipment', { params }).then((r) => r.data)
}

export function getEquipment(id: number): Promise<Equipment> {
  return http.get(`/equipment/${id}`).then((r) => r.data)
}

export function createEquipment(payload: Partial<Equipment>): Promise<Equipment> {
  return http.post('/equipment', payload).then((r) => r.data)
}

export function updateEquipment(id: number, payload: Partial<Equipment>): Promise<Equipment> {
  return http.put(`/equipment/${id}`, payload).then((r) => r.data)
}

export function deleteEquipment(id: number): Promise<void> {
  return http.delete(`/equipment/${id}`).then(() => undefined)
}

export function listLabs(): Promise<Lab[]> {
  return http.get('/labs').then((r) => r.data)
}

export function borrow(equipmentId: number, dueTime: string, purpose?: string): Promise<Loan> {
  return http.post('/loans', { equipmentId, dueTime, purpose }).then((r) => r.data)
}

export function returnLoan(id: number): Promise<Loan> {
  return http.post(`/loans/${id}/return`).then((r) => r.data)
}

export function myLoans(): Promise<Loan[]> {
  return http.get('/loans/mine').then((r) => r.data)
}

export function allLoans(): Promise<Loan[]> {
  return http.get('/loans').then((r) => r.data)
}

export function overdueLoans(): Promise<Loan[]> {
  return http.get('/loans/overdue').then((r) => r.data)
}

export function getDashboard(): Promise<DashboardStats> {
  return http.get('/stats/dashboard').then((r) => r.data)
}

export function listUsers(): Promise<UserResponse[]> {
  return http.get('/users').then((r) => r.data)
}

export function listCategories(): Promise<string[]> {
  return http.get('/equipment/categories').then((r) => r.data)
}

export function createLab(payload: { name: string; location?: string }): Promise<Lab> {
  return http.post('/labs', payload).then((r) => r.data)
}

export function updateLab(id: number, payload: { name?: string; location?: string }): Promise<Lab> {
  return http.put(`/labs/${id}`, payload).then((r) => r.data)
}

export function deleteLab(id: number): Promise<void> {
  return http.delete(`/labs/${id}`).then(() => undefined)
}
