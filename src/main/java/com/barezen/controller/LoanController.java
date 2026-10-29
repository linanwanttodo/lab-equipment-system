package com.barezen.controller;

import com.barezen.dto.BorrowRequest;
import com.barezen.dto.LoanResponse;
import com.barezen.exception.ApiException;
import com.barezen.security.AppUser;
import com.barezen.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /** 学生申请借用 */
    @PostMapping
    public LoanResponse borrow(@Valid @RequestBody BorrowRequest req, Authentication authentication) {
        return loanService.borrow(currentUserId(authentication), req);
    }

    /** 归还（本人或管理员） */
    @PostMapping("/{id}/return")
    public LoanResponse doReturn(@PathVariable Long id, Authentication authentication) {
        return loanService.doReturn(currentUserId(authentication), id);
    }

    /** 我的借用记录 */
    @GetMapping("/mine")
    public List<LoanResponse> mine(Authentication authentication) {
        return loanService.mine(currentUserId(authentication));
    }

    /** 管理员：全部借用记录 */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<LoanResponse> all() {
        return loanService.all();
    }

    /** 管理员：逾期列表 */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    public List<LoanResponse> overdue() {
        return loanService.overdue();
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUser appUser) {
            return appUser.getId();
        }
        throw new ApiException(401, "未登录");
    }
}
