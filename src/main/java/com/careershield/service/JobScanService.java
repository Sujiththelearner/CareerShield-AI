package com.careershield.service;

import com.careershield.dto.request.JobScanRequest;
import com.careershield.dto.response.JobScanSummaryResponse;
import com.careershield.dto.response.MLPredictionResponse;
import com.careershield.dto.response.RiskFactorDTO;
import com.careershield.dto.response.RiskReportResponse;
import com.careershield.entity.JobScan;
import com.careershield.entity.RiskAssessment;
import com.careershield.entity.RiskFactor;
import com.careershield.entity.User;
import com.careershield.enums.ScanStatus;
import com.careershield.exception.ResourceNotFoundException;
import com.careershield.repository.JobScanRepository;
import com.careershield.repository.RiskAssessmentRepository;
import com.careershield.repository.SavedJobRepository;
import com.careershield.repository.UserRepository;
import com.careershield.service.rule.RiskFactorRule;
import com.careershield.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Core Service orchestrating Job Scan Processing, Multi-factor Rule Evaluation (Strategy Pattern),
 * Machine Learning Inference, and Safety Report Assembly.
 */
@Service
public class JobScanService {

    private final List<RiskFactorRule> rules;
    private final MLPredictionService mlPredictionService;
    private final RiskScoreService riskScoreService;
    private final JobScanRepository jobScanRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;

    @Value("${careershield.upload.dir:uploads/evidence}")
    private String uploadDir;

    public JobScanService(List<RiskFactorRule> rules,
                          MLPredictionService mlPredictionService,
                          RiskScoreService riskScoreService,
                          JobScanRepository jobScanRepository,
                          RiskAssessmentRepository riskAssessmentRepository,
                          SavedJobRepository savedJobRepository,
                          UserRepository userRepository) {
        this.rules = rules;
        this.mlPredictionService = mlPredictionService;
        this.riskScoreService = riskScoreService;
        this.jobScanRepository = jobScanRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.savedJobRepository = savedJobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RiskReportResponse analyzeJob(JobScanRequest request, MultipartFile file) throws IOException {
        // 1. Resolve User
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }
        if (user == null) {
            // Default to first user or create guest student
            user = userRepository.findAll().stream().findFirst().orElseGet(() -> {
                User guest = new User("Student Explorer", "guest@student.edu", "nopass", com.careershield.enums.Role.STUDENT, "Campus Security");
                return userRepository.save(guest);
            });
        }

        // 2. Process file attachment if present
        String attachmentFileName = null;
        if (file != null && !file.isEmpty()) {
            attachmentFileName = FileUploadUtil.saveFile(uploadDir, file);
        } else if (request.getAttachmentPath() != null) {
            attachmentFileName = request.getAttachmentPath();
        }

        // 3. Create JobScan Entity
        JobScan jobScan = new JobScan(
                user,
                request.getJobTitle().trim(),
                request.getCompanyName().trim(),
                request.getJobUrl() != null ? request.getJobUrl().trim() : null,
                request.getRecruiterEmail() != null ? request.getRecruiterEmail().trim() : null,
                request.getSalaryInfo() != null ? request.getSalaryInfo().trim() : null,
                request.getLocation() != null ? request.getLocation().trim() : "Remote / Unspecified",
                request.getJobDescription().trim(),
                attachmentFileName
        );
        jobScan.setScanStatus(ScanStatus.IN_PROGRESS);

        // 4. Execute Multi-Factor Rule Engine (Strategy Pattern via List<RiskFactorRule>)
        List<RiskFactor> evaluatedFactors = new ArrayList<>();
        for (RiskFactorRule rule : rules) {
            Optional<RiskFactor> factorOpt = rule.evaluate(jobScan);
            factorOpt.ifPresent(evaluatedFactors::add);
        }

        // 5. Query Machine Learning Microservice (with fallback)
        MLPredictionResponse mlPrediction = mlPredictionService.predict(jobScan.getJobDescription());

        // 6. Compute Unified Risk Assessment & Score
        RiskAssessment assessment = riskScoreService.calculateAssessment(jobScan, evaluatedFactors, mlPrediction);
        jobScan.setRiskAssessment(assessment);
        jobScan.setScanStatus(ScanStatus.COMPLETED);

        // 7. Persist to MySQL
        JobScan savedScan = jobScanRepository.save(jobScan);

        // 8. Convert to response DTO
        return mapToReportResponse(savedScan, user.getId());
    }

    @Transactional(readOnly = true)
    public RiskReportResponse getReportByScanId(Long scanId, Long currentUserId) {
        JobScan jobScan = jobScanRepository.findById(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Scan report not found with ID: " + scanId));

        return mapToReportResponse(jobScan, currentUserId);
    }

    @Transactional(readOnly = true)
    public List<JobScanSummaryResponse> getUserScans(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<JobScan> scans = jobScanRepository.findByUserOrderByCreatedAtDesc(user);

        return scans.stream().map(scan -> {
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
                    scan.getCreatedAt(),
                    ra != null ? ra.getRiskFactors().size() : 0
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobScanSummaryResponse> getRecentScans(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<JobScan> scans = user != null
                ? jobScanRepository.findTop5ByUserOrderByCreatedAtDesc(user)
                : jobScanRepository.findTop10ByOrderByCreatedAtDesc();

        return scans.stream().map(scan -> {
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
                    scan.getCreatedAt(),
                    ra != null ? ra.getRiskFactors().size() : 0
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteScan(Long scanId, Long userId) {
        JobScan jobScan = jobScanRepository.findById(scanId)
                .orElseThrow(() -> new ResourceNotFoundException("Scan not found with ID: " + scanId));

        jobScanRepository.delete(jobScan);
    }

    private RiskReportResponse mapToReportResponse(JobScan scan, Long userId) {
        RiskAssessment ra = scan.getRiskAssessment();
        RiskReportResponse resp = new RiskReportResponse();

        resp.setScanId(scan.getId());
        resp.setJobTitle(scan.getJobTitle());
        resp.setCompanyName(scan.getCompanyName());
        resp.setJobUrl(scan.getJobUrl());
        resp.setRecruiterEmail(scan.getRecruiterEmail());
        resp.setSalaryInfo(scan.getSalaryInfo());
        resp.setLocation(scan.getLocation());
        resp.setJobDescription(scan.getJobDescription());
        resp.setAttachmentPath(scan.getAttachmentPath());
        resp.setScannedAt(scan.getCreatedAt());

        if (ra != null) {
            resp.setOverallSafetyScore(ra.getOverallSafetyScore());
            resp.setRiskLevel(ra.getRiskLevel());
            resp.setVerdict(ra.getVerdict());
            resp.setMlConfidenceScore(ra.getMlConfidenceScore());
            resp.setMlModelName("Hybrid Multi-Engine (Java Strategy + NLP Classifier)");
            resp.setRuleEngineScore(ra.getRuleEngineScore());
            resp.setSummaryExplanation(ra.getSummaryExplanation());
            resp.setSafetyRecommendation(ra.getSafetyRecommendation());

            List<RiskFactorDTO> factorDTOs = ra.getRiskFactors().stream()
                    .map(f -> new RiskFactorDTO(
                            f.getId(),
                            f.getCategory(),
                            f.getTitle(),
                            f.getDescription(),
                            f.getWhyItMatters(),
                            f.getSeverity(),
                            f.getScorePenalty()
                    ))
                    .collect(Collectors.toList());
            resp.setFactors(factorDTOs);
        }

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                boolean isSaved = savedJobRepository.existsByUserAndJobScan(user, scan);
                resp.setSaved(isSaved);
            }
        }

        return resp;
    }
}
