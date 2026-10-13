package com.planora.controller;

import com.planora.dto.DashboardAnalyticsDTO;
import com.planora.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "User dashboard analytics, charts and summaries")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get aggregated dashboard analytics for current user")
    public ResponseEntity<DashboardAnalyticsDTO> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getUserDashboard(userDetails.getUsername()));
    }
}
