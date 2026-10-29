export interface Equipment {
  id: number
  code: string
  name: string
  category: string
  labId: number | null
  labName: string | null
  totalQty: number
  availableQty: number
  deposit: number
  status: 'AVAILABLE' | 'MAINTENANCE'
  description: string | null
  createdAt: string
}

export interface Loan {
  id: number
  equipmentId: number | null
  equipmentName: string
  equipmentCode: string
  userId: number | null
  username: string
  userName: string
  purpose: string | null
  borrowTime: string
  dueTime: string
  returnTime: string | null
  status: 'BORROWING' | 'RETURNED' | 'OVERDUE'
  overdueDays: number
  remark: string | null
}

export interface Lab {
  id: number
  name: string
  location: string
  managerId: number | null
  equipmentCount: number
}

export interface UserResponse {
  id: number
  username: string
  name: string
  contact: string
  role: 'STUDENT' | 'ADMIN'
  createdAt: string
}

export interface CategoryCount {
  name: string
  count: number
}

export interface LabCount {
  labId: number | null
  labName: string
  count: number
}

export interface DashboardStats {
  userCount: number
  labCount: number
  equipmentCount: number
  equipmentAvailable: number
  loanTotal: number
  loanBorrowing: number
  loanOverdue: number
  loanReturned: number
  categoryStats: CategoryCount[]
  labStats: LabCount[]
  recentLoans: Loan[]
  overdueTop: Loan[]
}
