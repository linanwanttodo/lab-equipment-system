package com.barezen.lab.entity;

import java.sql.Timestamp;

/** 借用记录实体：status ACTIVE 借用中 / RETURNED 已归还（逾期由应还时间动态判定） */
public class Loan {
    private Long id;
    private Long userId;
    private String username;
    private String userName;
    private Long equipmentId;
    private String equipmentName;
    private String equipmentCode;
    private String purpose;
    private Timestamp borrowTime;
    private Timestamp dueTime;
    private Timestamp returnTime;
    private String status;
    private Timestamp createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String equipmentCode) { this.equipmentCode = equipmentCode; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public Timestamp getBorrowTime() { return borrowTime; }
    public void setBorrowTime(Timestamp borrowTime) { this.borrowTime = borrowTime; }
    public Timestamp getDueTime() { return dueTime; }
    public void setDueTime(Timestamp dueTime) { this.dueTime = dueTime; }
    public Timestamp getReturnTime() { return returnTime; }
    public void setReturnTime(Timestamp returnTime) { this.returnTime = returnTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** 逾期天数：已归还按应还到归还计算，未归还按应还到当前计算 */
    public long getOverdueDays() {
        Timestamp deadline = returnTime != null ? returnTime : new Timestamp(System.currentTimeMillis());
        if (!"OVERDUE".equals(status) && (returnTime != null || deadline.before(dueTime))) {
            return 0;
        }
        long diff = deadline.getTime() - dueTime.getTime();
        return diff > 0 ? diff / (24 * 3600 * 1000L) : 0;
    }
}
