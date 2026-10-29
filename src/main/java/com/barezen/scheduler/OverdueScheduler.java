package com.barezen.scheduler;

import com.barezen.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueScheduler.class);

    private final LoanService loanService;

    public OverdueScheduler(LoanService loanService) {
        this.loanService = loanService;
    }

    /** 每 60 秒扫描一次：标记逾期并发送提醒邮件（首次标记后不会重复提醒）。 */
    @Scheduled(fixedDelay = 60_000L, initialDelay = 15_000L)
    public void scan() {
        try {
            loanService.scanAndNotifyOverdue();
        } catch (Exception e) {
            log.error("逾期扫描失败: {}", e.getMessage());
        }
    }
}
