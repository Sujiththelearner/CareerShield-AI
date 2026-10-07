package com.careershield.controller;

import com.careershield.dto.response.ApiResponse;
import com.careershield.dto.response.DashboardStatsResponse;
import com.careershield.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStudentDashboardStats(
            @RequestParam("userId") Long userId) {
        DashboardStatsResponse stats = dashboardService.getStudentStats(userId);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
