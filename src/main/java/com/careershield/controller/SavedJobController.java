package com.careershield.controller;

import com.careershield.dto.response.ApiResponse;
import com.careershield.dto.response.JobScanSummaryResponse;
import com.careershield.service.SavedJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobController {

    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    @PostMapping("/{scanId}")
    public ResponseEntity<ApiResponse<Boolean>> toggleSavedJob(
            @PathVariable("scanId") Long scanId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "notes", required = false) String notes) {
        boolean isSaved = savedJobService.toggleBookmark(userId, scanId, notes);
        String msg = isSaved ? "Job saved to your bookmarks" : "Job removed from bookmarks";
        return ResponseEntity.ok(ApiResponse.ok(msg, isSaved));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<JobScanSummaryResponse>>> getMySavedJobs(
            @RequestParam("userId") Long userId) {
        List<JobScanSummaryResponse> list = savedJobService.getSavedJobs(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
