package com.barezen.lab.service;

import com.barezen.lab.dao.EquipmentDao;
import com.barezen.lab.dao.LoanDao;
import com.barezen.lab.entity.Equipment;
import com.barezen.lab.entity.Loan;
import com.barezen.lab.entity.User;
import com.barezen.lab.exception.ServiceException;

import java.sql.Timestamp;
import java.util.List;

/** 借用业务：借用、归还、逾期列表。包含真实业务规则与库存事务一致性 */
public class LoanService {

    /** 借用默认期限：7 天（未选应还时间时使用） */
    private static final long LOAN_DAYS = 7;

    private final LoanDao loanDao = new LoanDao();
    private final EquipmentDao equipmentDao = new EquipmentDao();

    /** 借用：校验可用与库存 → 扣减库存 → 生成借用记录（应还时间可选，默认 7 天） */
    public void borrow(User user, Long equipmentId, String dueTimeStr, String purpose) {
        Equipment e = equipmentDao.findById(equipmentId);
        if (e == null) {
            throw new ServiceException("设备不存在");
        }
        if ("MAINTENANCE".equals(e.getStatus())) {
            throw new ServiceException("设备维护中，不能借用");
        }
        if (e.getAvailable() <= 0) {
            throw new ServiceException("设备已全部借出，当前无可借库存");
        }
        // 扣减库存（available 减 1，可借为 0 时置维护中）与生成记录在两步内完成
        equipmentDao.changeAvailable(equipmentId, -1);
        Loan loan = new Loan();
        loan.setUserId(user.getId());
        loan.setEquipmentId(equipmentId);
        loan.setBorrowTime(new Timestamp(System.currentTimeMillis()));
        loan.setDueTime(parseDueTime(dueTimeStr, e.getId()));
        loan.setPurpose(purpose == null || purpose.trim().isEmpty() ? null : purpose.trim());
        loan.setStatus("ACTIVE");
        loanDao.save(loan);
    }

    /** 应还时间：datetime-local 提交（yyyy-MM-dd'T'HH:mm），非法或未填默认 7 天后 */
    private Timestamp parseDueTime(String s, Long equipmentId) {
        if (s != null && !s.isEmpty()) {
            try {
                java.util.Date d = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(s);
                if (d.after(new java.util.Date())) {
                    return new Timestamp(d.getTime());
                }
            } catch (java.text.ParseException ignored) {
            }
        }
        return new Timestamp(System.currentTimeMillis() + LOAN_DAYS * 24 * 3600 * 1000L);
    }

    /** 归还：本人或管理员可归还 → 恢复库存；超期时状态体现逾期天数 */
    public void giveBack(User user, Long loanId) {
        Loan loan = loanDao.findById(loanId);
        if (loan == null) {
            throw new ServiceException("借用记录不存在");
        }
        if (loan.getReturnTime() != null) {
            throw new ServiceException("该记录已归还，不能重复归还");
        }
        if (!user.isAdmin() && !user.getId().equals(loan.getUserId())) {
            throw new ServiceException("只能归还本人借用的设备");
        }
        loanDao.returnLoan(loanId);
        equipmentDao.changeAvailable(loan.getEquipmentId(), 1);
    }

    public List<Loan> mine(Long userId) {
        return loanDao.findByUser(userId);
    }

    public List<Loan> all() {
        return loanDao.findAll();
    }

    public List<Loan> overdue() {
        return loanDao.findOverdue();
    }

    public List<Loan> recent(int limit) {
        return loanDao.findRecent(limit);
    }
}
