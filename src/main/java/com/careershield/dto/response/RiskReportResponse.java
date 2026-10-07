package com.careershield.dto.response;

import com.careershield.enums.RiskLevel;
import com.careershield.enums.VerificationVerdict;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Detailed explainable safety report model returned to the student.
 */
public class RiskReportResponse {

    private Long scanId;
    private String jobTitle;
    private String companyName;
    private String jobUrl;
    private String recruiterEmail;
    private String salaryInfo;
    private String location;
    private String jobDescription;
    private String attachmentPath;

    private int overallSafetyScore; // 0 to 100
    private RiskLevel riskLevel;
    private VerificationVerdict verdict;
    private String verdictDescription;
    private Double mlConfidenceScore;
    private String mlModelName;
    private Double ruleEngineScore;

    private String summaryExplanation;
    private String safetyRecommendation;

    private List<RiskFactorDTO> factors = new ArrayList<>();
    private LocalDateTime scannedAt;
    private boolean saved;

    public RiskReportResponse() {
    }

    // Getters and Setters
    public Long getScanId() {
        return scanId;
    }

    public void setScanId(Long scanId) {
        this.scanId = scanId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getSalaryInfo() {
        return salaryInfo;
    }

    public void setSalaryInfo(String salaryInfo) {
        this.salaryInfo = salaryInfo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public int getOverallSafetyScore() {
        return overallSafetyScore;
    }

    public void setOverallSafetyScore(int overallSafetyScore) {
        this.overallSafetyScore = overallSafetyScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public VerificationVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(VerificationVerdict verdict) {
        this.verdict = verdict;
        if (verdict != null) {
            this.verdictDescription = verdict.getDescription();
        }
    }

    public String getVerdictDescription() {
        return verdictDescription;
    }

    public void setVerdictDescription(String verdictDescription) {
        this.verdictDescription = verdictDescription;
    }

    public Double getMlConfidenceScore() {
        return mlConfidenceScore;
    }

    public void setMlConfidenceScore(Double mlConfidenceScore) {
        this.mlConfidenceScore = mlConfidenceScore;
    }

    public String getMlModelName() {
        return mlModelName;
    }

    public void setMlModelName(String mlModelName) {
        this.mlModelName = mlModelName;
    }

    public Double getRuleEngineScore() {
        return ruleEngineScore;
    }

    public void setRuleEngineScore(Double ruleEngineScore) {
        this.ruleEngineScore = ruleEngineScore;
    }

    public String getSummaryExplanation() {
        return summaryExplanation;
    }

    public void setSummaryExplanation(String summaryExplanation) {
        this.summaryExplanation = summaryExplanation;
    }

    public String getSafetyRecommendation() {
        return safetyRecommendation;
    }

    public void setSafetyRecommendation(String safetyRecommendation) {
        this.safetyRecommendation = safetyRecommendation;
    }

    public List<RiskFactorDTO> getFactors() {
        return factors;
    }

    public void setFactors(List<RiskFactorDTO> factors) {
        this.factors = factors;
    }

    public LocalDateTime getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(LocalDateTime scannedAt) {
        this.scannedAt = scannedAt;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
