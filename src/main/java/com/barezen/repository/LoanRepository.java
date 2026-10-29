package com.barezen.repository;

import com.barezen.entity.Loan;
import com.barezen.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdOrderByBorrowTimeDesc(Long userId);

    List<Loan> findAllByOrderByBorrowTimeDesc();

    List<Loan> findByStatus(LoanStatus status);

    /** 借用中且已过应还时间 —— 用于逾期扫描 */
    List<Loan> findByStatusAndDueTimeBefore(LoanStatus status, LocalDateTime dueTimeBefore);
}
