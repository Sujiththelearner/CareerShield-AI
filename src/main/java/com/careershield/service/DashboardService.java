package com.careershield.service;

import com.careershield.dto.response.DashboardStatsResponse;
import com.careershield.entity.JobScan;
import com.careershield.entity.RiskAssessment;
import com.careershield.entity.User;
import com.careershield.enums.RiskLevel;
import com.careershield.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service providing aggregated statistical metrics for Student & Admin dashboards.
 * Demonstrates Java Streams API, aggregations, and repository queries.
 */
@Service
public class DashboardService {

    private final JobScanRepository jobScanRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final EvidenceRepository evidenceRepository;
    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;

    public DashboardService(JobScanRepository jobScanRepository,
                            RiskAssessmentRepository riskAssessmentRepository,
                            EvidenceRepository evidenceRepository,
                            SavedJobRepository savedJobRepository,
                            UserRepository userRepository) {
        this.jobScanRepository = jobScanRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.evidenceRepository = evidenceRepository;
        this.savedJobRepository = savedJobRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStudentStats(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new DashboardStatsResponse(0, 0, 0, 0, 0, 0, 100);
        }

        List<JobScan> userScans = jobScanRepository.findByUserOrderByCreatedAtDesc(user);
        long totalScans = userScans.size();

        long safeCount = 0;
        long suspiciousCount = 0;
        long highRiskCount = 0;
        int totalScore = 0;

        for (JobScan scan : userScans) {
            RiskAssessment ra = scan.getRiskAssessment();
            if (ra != null) {
                totalScore += ra.getOverallSafetyScore();
                if (ra.getRiskLevel() == RiskLevel.LOW) {
                    safeCount++;
                } else if (ra.getRiskLevel() == RiskLevel.MEDIUM) {
                    suspiciousCount++;
                } else {
                    highRiskCount++;
                }
            }
        }

        int avgScore = totalScans > 0 ? (int) Math.round((double) totalScore / totalScans) : 100;
        long evidenceCount = evidenceRepository.countByUser(user);
        long savedJobsCount = savedJobRepository.countByUser(user);

        return new DashboardStatsResponse(
                totalScans,
                safeCount,
                suspiciousCount,
                highRiskCount,
                evidenceCount,
                savedJobsCount,
                avgScore
        );
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getGlobalAdminStats() {
        long totalScans = jobScanRepository.count();
        long safeCount = riskAssessmentRepository.countByRiskLevel(RiskLevel.LOW);
        long suspiciousCount = riskAssessmentRepository.countByRiskLevel(RiskLevel.MEDIUM);
        long highRiskCount = riskAssessmentRepository.countByRiskLevel(RiskLevel.HIGH)
                + riskAssessmentRepository.countByRiskLevel(RiskLevel.CRITICAL);
        long evidenceCount = evidenceRepository.count();
        long savedJobsCount = savedJobRepository.count();
        Double avg = riskAssessmentRepository.calculateAverageSafetyScore();
        int avgScore = avg != null ? (int) Math.round(avg) : 100;

        return new DashboardStatsResponse(
                totalScans,
                safeCount,
                suspiciousCount,
                highRiskCount,
                evidenceCount,
                savedJobsCount,
                avgScore
        );
    }
}
