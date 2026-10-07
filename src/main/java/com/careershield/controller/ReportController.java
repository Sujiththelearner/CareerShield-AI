package com.careershield.controller;

import com.careershield.dto.response.ApiResponse;
import com.careershield.dto.response.RiskReportResponse;
import com.careershield.service.JobScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final JobScanService jobScanService;

    public ReportController(JobScanService jobScanService) {
        this.jobScanService = jobScanService;
    }

    @GetMapping("/{scanId}")
    public ResponseEntity<ApiResponse<RiskReportResponse>> getReport(
            @PathVariable("scanId") Long scanId,
            @RequestParam(value = "userId", required = false) Long userId) {
        RiskReportResponse report = jobScanService.getReportByScanId(scanId, userId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }
}
