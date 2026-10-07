package com.careershield.service;

import com.careershield.dto.response.JobScanSummaryResponse;
import com.careershield.entity.JobScan;
import com.careershield.entity.RiskAssessment;
import com.careershield.entity.SavedJob;
import com.careershield.entity.User;
import com.careershield.exception.ResourceNotFoundException;
import com.careershield.repository.JobScanRepository;
import com.careershield.repository.SavedJobRepository;
import com.careershield.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobScanRepository jobScanRepository;
    private final UserRepository userRepository;

    public SavedJobService(SavedJobRepository savedJobRepository,
                           JobScanRepository jobScanRepository,
                           UserRepository userRepository) {
        this.savedJobRepository = savedJobRepository;
        this.jobScanRepository = jobScanRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean toggleBookmark(Long userId, Long scanId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        JobScan scan = jobScanRepository.findById(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Scan not found with ID: " + scanId));

        Optional<SavedJob> existing = savedJobRepository.findByUserAndJobScan(user, scan);
        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            return false; // Removed
        } else {
            SavedJob saved = new SavedJob(user, scan, notes != null ? notes : "Verified opportunity");
            savedJobRepository.save(saved);
            return true; // Added
        }
    }

    @Transactional(readOnly = true)
    public List<JobScanSummaryResponse> getSavedJobs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<SavedJob> savedList = savedJobRepository.findByUserOrderByCreatedAtDesc(user);
        return savedList.stream().map(saved -> {
            JobScan scan = saved.getJobScan();
            RiskAssessment ra = scan.getRiskAssessment();
            return new JobScanSummaryResponse(
                    scan.getId(),
                    scan.getJobTitle(),
                    scan.getCompanyName(),
                    scan.getLocation(),
                    ra != null ? ra.getOverallSafetyScore() : 0,
                    ra != null ? ra.getRiskLevel() : com.careershield.enums.RiskLevel.LOW,
                    ra != null ? ra.getVerdict() : com.careershield.enums.VerificationVerdict.GENUINE,
                    scan.getScanStatus(),
                    saved.getSavedAt(),
                    ra != null ? ra.getRiskFactors().size() : 0
            );
        }).collect(Collectors.toList());
    }
}
