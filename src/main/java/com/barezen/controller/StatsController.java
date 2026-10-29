package com.barezen.controller;

import com.barezen.dto.DashboardStats;
import com.barezen.service.StatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public DashboardStats dashboard() {
        return statsService.dashboard();
    }
}
