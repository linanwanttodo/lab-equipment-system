package com.barezen.dto;

import com.barezen.entity.Equipment;
import com.barezen.entity.Loan;
import com.barezen.entity.LoanStatus;
import com.barezen.entity.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LoanResponse {

    private final Long id;
    private final Long equipmentId;
    private final String equipmentName;
    private final String equipmentCode;
    private final Long userId;
    private final String username;
    private final String userName;
    private final String purpose;
    private final LocalDateTime borrowTime;
    private final LocalDateTime dueTime;
    private final LocalDateTime returnTime;
    private final LoanStatus status;
    private final Integer overdueDays;
    private final String remark;

    public LoanResponse(Long id, Long equipmentId, String equipmentName, String equipmentCode,
                        Long userId, String username, String userName, String purpose,
                        LocalDateTime borrowTime, LocalDateTime dueTime, LocalDateTime returnTime,
                        LoanStatus status, Integer overdueDays, String remark) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentCode = equipmentCode;
        this.userId = userId;
        this.username = username;
        this.userName = userName;
        this.purpose = purpose;
        this.borrowTime = borrowTime;
        this.dueTime = dueTime;
        this.returnTime = returnTime;
        this.status = status;
        this.overdueDays = overdueDays;
        this.remark = remark;
    }

    public Long getId() { return id; }
    public Long getEquipmentId() { return equipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public String getEquipmentCode() { return equipmentCode; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserName() { return userName; }
    public String getPurpose() { return purpose; }
    public LocalDateTime getBorrowTime() { return borrowTime; }
    public LocalDateTime getDueTime() { return dueTime; }
    public LocalDateTime getReturnTime() { return returnTime; }
    public LoanStatus getStatus() { return status; }
    public Integer getOverdueDays() { return overdueDays; }
    public String getRemark() { return remark; }

    /** 从实体构造响应：已归还的逾期天数按实际归还时间计算，未归还的按当前时间实时派生。 */
    public static LoanResponse from(Loan loan) {
        LocalDateTime now = LocalDateTime.now();
        int days = 0;
        LoanStatus display = loan.getStatus();
        if (loan.getStatus() == LoanStatus.RETURNED) {
            if (loan.getReturnTime() != null && loan.getDueTime() != null) {
                days = Math.max(0, (int) ChronoUnit.DAYS.between(loan.getDueTime(), loan.getReturnTime()));
            }
        } else if (loan.getDueTime() != null && loan.getDueTime().isBefore(now)) {
            display = LoanStatus.OVERDUE;
            days = Math.max(0, (int) ChronoUnit.DAYS.between(loan.getDueTime(), now));
        }
        Equipment eq = loan.getEquipment();
        User user = loan.getUser();
        return new LoanResponse(
                loan.getId(),
                eq != null ? eq.getId() : null,
                eq != null ? eq.getName() : "",
                eq != null ? eq.getCode() : "",
                user != null ? user.getId() : null,
                user != null ? user.getUsername() : "",
                user != null ? user.getName() : "",
                loan.getPurpose(),
                loan.getBorrowTime(),
                loan.getDueTime(),
                loan.getReturnTime(),
                display,
                days,
                loan.getRemark());
    }
}
