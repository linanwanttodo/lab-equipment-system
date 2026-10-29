package com.barezen.service;

import com.barezen.dto.DashboardStats;
import com.barezen.dto.LoanResponse;
import com.barezen.entity.Loan;
import com.barezen.entity.LoanStatus;
import com.barezen.repository.EquipmentRepository;
import com.barezen.repository.LabRepository;
import com.barezen.repository.LoanRepository;
import com.barezen.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 后台统计数据汇总。 */
@Service
public class StatsService {

    private final UserRepository userRepository;
    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final LoanRepository loanRepository;

    public StatsService(UserRepository userRepository, LabRepository labRepository,
                        EquipmentRepository equipmentRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats dashboard() {
        DashboardStats s = new DashboardStats();
        s.userCount = userRepository.count();
        s.labCount = labRepository.count();
        s.equipmentCount = equipmentRepository.count();
        s.equipmentAvailable = equipmentRepository.findAll().stream()
                .mapToLong(e -> e.getAvailableQty()).sum();

        List<Loan> all = loanRepository.findAll();
        s.loanTotal = all.size();

        // 与列表口径一致：借用中但已过应还时间的，实时按逾期计
        LocalDateTime now = LocalDateTime.now();
        long borrowing = 0;
        long overdue = 0;
        for (Loan l : all) {
            if (l.getStatus() == LoanStatus.RETURNED) {
                continue;
            }
            if (l.getDueTime() != null && l.getDueTime().isBefore(now)) {
                overdue++;
            } else {
                borrowing++;
            }
        }
        s.loanBorrowing = borrowing;
        s.loanOverdue = overdue;
        s.loanReturned = all.stream().filter(l -> l.getStatus() == LoanStatus.RETURNED).count();

        // 设备类别分布
        Map<String, Long> byCategory = new HashMap<>();
        equipmentRepository.findAll().forEach(e -> {
            String key = e.getCategory() == null || e.getCategory().isBlank() ? "未分类" : e.getCategory();
            byCategory.merge(key, 1L, Long::sum);
        });
        s.categoryStats = byCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new DashboardStats.CategoryCount(e.getKey(), e.getValue()))
                .toList();

        // 实验室设备分布
        Map<Long, Long> byLab = new HashMap<>();
        equipmentRepository.findAll().forEach(e -> {
            if (e.getLab() != null) {
                byLab.merge(e.getLab().getId(), 1L, Long::sum);
            }
        });
        s.labStats = labRepository.findAll().stream()
                .map(l -> new DashboardStats.LabCount(l.getId(), l.getName(), byLab.getOrDefault(l.getId(), 0L)))
                .sorted(Comparator.comparing((DashboardStats.LabCount c) -> c.count).reversed())
                .toList();

        // 最近借用（按借出时间倒序取 10）
        s.recentLoans = loanRepository.findAllByOrderByBorrowTimeDesc().stream()
                .map(LoanResponse::from)
                .limit(10)
                .toList();

        // 逾期 TOP（按逾期天数倒序取 10）
        List<Loan> overdueLoans = new ArrayList<>(loanRepository.findByStatus(LoanStatus.OVERDUE));
        overdueLoans.addAll(loanRepository.findByStatusAndDueTimeBefore(LoanStatus.BORROWING, now));
        s.overdueTop = overdueLoans.stream()
                .map(LoanResponse::from)
                .sorted(Comparator.comparing((LoanResponse r) -> r.getOverdueDays()).reversed())
                .limit(10)
                .toList();

        return s;
    }
}
