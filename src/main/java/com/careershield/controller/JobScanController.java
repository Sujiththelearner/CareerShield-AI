package com.careershield.controller;

import com.careershield.dto.request.JobScanRequest;
import com.careershield.dto.response.ApiResponse;
import com.careershield.dto.response.JobScanSummaryResponse;
import com.careershield.dto.response.RiskReportResponse;
import com.careershield.service.JobScanService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scans")
public class JobScanController {

    private final JobScanService jobScanService;

    public JobScanController(JobScanService jobScanService) {
        this.jobScanService = jobScanService;
    }

    /**
     * Submit job metadata + description via JSON payload.
     */
    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<RiskReportResponse>> analyzeJobJson(
            @Valid @RequestBody JobScanRequest request) throws IOException {
        RiskReportResponse report = jobScanService.analyzeJob(request, null);
        return ResponseEntity.ok(ApiResponse.ok("Job analysis completed successfully", report));
    }

    /**
     * Submit job metadata + optional screenshot / PDF offer letter attachment.
     */
    @PostMapping(value = "/upload-analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RiskReportResponse>> analyzeJobMultipart(
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("companyName") String companyName,
            @RequestParam(value = "jobUrl", required = false) String jobUrl,
            @RequestParam(value = "recruiterEmail", required = false) String recruiterEmail,
            @RequestParam(value = "salaryInfo", required = false) String salaryInfo,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        JobScanRequest request = new JobScanRequest(
                userId, jobTitle, companyName, jobUrl, recruiterEmail, salaryInfo, location, jobDescription, null
        );

        RiskReportResponse report = jobScanService.analyzeJob(request, file);
        return ResponseEntity.ok(ApiResponse.ok("Job scan & attachment analyzed successfully", report));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<JobScanSummaryResponse>>> getUserScans(
            @RequestParam("userId") Long userId) {
        List<JobScanSummaryResponse> list = jobScanService.getUserScans(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<JobScanSummaryResponse>>> getRecentScans(
            @RequestParam(value = "userId", required = false) Long userId) {
        List<JobScanSummaryResponse> list = jobScanService.getRecentScans(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiskReportResponse>> getScanReport(
            @PathVariable("id") Long scanId,
            @RequestParam(value = "userId", required = false) Long userId) {
        RiskReportResponse report = jobScanService.getReportByScanId(scanId, userId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteScan(
            @PathVariable("id") Long scanId,
            @RequestParam(value = "userId", required = false) Long userId) {
        jobScanService.deleteScan(scanId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Scan deleted successfully", null));
    }
}
