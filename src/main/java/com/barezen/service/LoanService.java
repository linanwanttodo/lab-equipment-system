package com.barezen.service;

import com.barezen.dto.BorrowRequest;
import com.barezen.dto.LoanResponse;
import com.barezen.entity.Equipment;
import com.barezen.entity.EquipmentStatus;
import com.barezen.entity.Loan;
import com.barezen.entity.LoanStatus;
import com.barezen.entity.Role;
import com.barezen.entity.User;
import com.barezen.exception.ApiException;
import com.barezen.mail.MailService;
import com.barezen.repository.EquipmentRepository;
import com.barezen.repository.LoanRepository;
import com.barezen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final LoanRepository loanRepository;
    private final MailService mailService;

    public LoanService(UserRepository userRepository, EquipmentRepository equipmentRepository,
                       LoanRepository loanRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.loanRepository = loanRepository;
        this.mailService = mailService;
    }

    /** 借用：校验设备可用 → 扣减库存 → 生成借用记录。 */
    @Transactional
    public LoanResponse borrow(Long userId, BorrowRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(404, "用户不存在"));
        Equipment eq = equipmentRepository.findById(req.getEquipmentId())
                .orElseThrow(() -> new ApiException(404, "设备不存在"));
        if (eq.getStatus() != EquipmentStatus.AVAILABLE) {
            throw new ApiException(409, "该设备当前不可借（维护中）");
        }
        if (eq.getAvailableQty() <= 0) {
            throw new ApiException(409, "该设备库存不足");
        }
        if (req.getDueTime() == null || !req.getDueTime().isAfter(LocalDateTime.now())) {
            throw new ApiException(400, "应还时间必须晚于当前时间");
        }
        eq.setAvailableQty(eq.getAvailableQty() - 1);
        equipmentRepository.save(eq);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setEquipment(eq);
        loan.setPurpose(req.getPurpose());
        loan.setBorrowTime(LocalDateTime.now());
        loan.setDueTime(req.getDueTime());
        loan.setStatus(LoanStatus.BORROWING);
        loan.setOverdueDays(0);
        loan = loanRepository.save(loan);
        log.info("用户 {} 借用设备 {}", user.getUsername(), eq.getCode());
        return LoanResponse.from(loan);
    }

    /** 归还：本人或管理员可操作；更新归还时间 → 恢复库存。 */
    @Transactional
    public LoanResponse doReturn(Long userId, Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ApiException(404, "借用记录不存在"));
        User actor = userRepository.findById(userId).orElseThrow(() -> new ApiException(404, "用户不存在"));
        boolean isOwner = loan.getUser() != null && loan.getUser().getId().equals(actor.getId());
        if (!isOwner && actor.getRole() != Role.ADMIN) {
            throw new ApiException(403, "无权限归还该设备");
        }
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new ApiException(409, "该设备已归还");
        }
        loan.setReturnTime(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        Equipment eq = loan.getEquipment();
        if (eq != null) {
            eq.setAvailableQty(eq.getAvailableQty() + 1);
            equipmentRepository.save(eq);
        }
        loan = loanRepository.save(loan);
        log.info("用户 {} 归还设备 loanId={}", actor.getUsername(), loanId);
        return LoanResponse.from(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> mine(Long userId) {
        return loanRepository.findByUserIdOrderByBorrowTimeDesc(userId).stream().map(LoanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> all() {
        return loanRepository.findAllByOrderByBorrowTimeDesc().stream().map(LoanResponse::from).toList();
    }

    /** 当前逾期 = 已标记 OVERDUE + 借用中但已过应还时间（保证展示实时准确）。 */
    @Transactional(readOnly = true)
    public List<LoanResponse> overdue() {
        List<Loan> list = new ArrayList<>(loanRepository.findByStatus(LoanStatus.OVERDUE));
        list.addAll(loanRepository.findByStatusAndDueTimeBefore(LoanStatus.BORROWING, LocalDateTime.now()));
        list.sort(Comparator.comparing(Loan::getDueTime));
        return list.stream().map(LoanResponse::from).toList();
    }

    /** 由定时任务调用：把“借用中且已过期”的记录标记为逾期，并发送提醒邮件。 */
    @Transactional
    public void scanAndNotifyOverdue() {
        List<Loan> overdue = loanRepository.findByStatusAndDueTimeBefore(LoanStatus.BORROWING, LocalDateTime.now());
        if (overdue.isEmpty()) {
            return;
        }
        for (Loan l : overdue) {
            l.setStatus(LoanStatus.OVERDUE);
            l.setOverdueDays((int) ChronoUnit.DAYS.between(l.getDueTime(), LocalDateTime.now()));
            loanRepository.save(l);
            mailService.sendOverdueNotice(l.getUser(), l);
        }
        log.info("逾期扫描：标记并提醒 {} 条记录", overdue.size());
    }

}
