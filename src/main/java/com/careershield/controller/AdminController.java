package com.careershield.controller;

import com.careershield.dto.response.ApiResponse;
import com.careershield.dto.response.DashboardStatsResponse;
import com.careershield.dto.response.EvidenceDTO;
import com.careershield.dto.response.JobScanSummaryResponse;
import com.careershield.service.DashboardService;
import com.careershield.service.EvidenceService;
import com.careershield.service.JobScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final EvidenceService evidenceService;
    private final JobScanService jobScanService;

    public AdminController(DashboardService dashboardService,
                           EvidenceService evidenceService,
                           JobScanService jobScanService) {
        this.dashboardService = dashboardService;
        this.evidenceService = evidenceService;
        this.jobScanService = jobScanService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getAdminMetrics() {
        DashboardStatsResponse stats = dashboardService.getGlobalAdminStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/evidence")
    public ResponseEntity<ApiResponse<List<EvidenceDTO>>> getReportedEvidence() {
        List<EvidenceDTO> list = evidenceService.getAllReportedEvidence();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/scans")
    public ResponseEntity<ApiResponse<List<JobScanSummaryResponse>>> getAllScans() {
        List<JobScanSummaryResponse> list = jobScanService.getRecentScans(null);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
