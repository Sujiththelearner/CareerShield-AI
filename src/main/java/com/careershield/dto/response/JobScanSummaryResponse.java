package com.careershield.dto.response;

import com.careershield.enums.RiskLevel;
import com.careershield.enums.ScanStatus;
import com.careershield.enums.VerificationVerdict;

import java.time.LocalDateTime;

public class JobScanSummaryResponse {

    private Long id;
    private String jobTitle;
    private String companyName;
    private String location;
    private int overallSafetyScore;
    private RiskLevel riskLevel;
    private VerificationVerdict verdict;
    private ScanStatus scanStatus;
    private LocalDateTime createdAt;
    private int riskFactorCount;

    public JobScanSummaryResponse() {
    }

    public JobScanSummaryResponse(Long id, String jobTitle, String companyName, String location,
                                  int overallSafetyScore, RiskLevel riskLevel, VerificationVerdict verdict,
                                  ScanStatus scanStatus, LocalDateTime createdAt, int riskFactorCount) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.overallSafetyScore = overallSafetyScore;
        this.riskLevel = riskLevel;
        this.verdict = verdict;
        this.scanStatus = scanStatus;
        this.createdAt = createdAt;
        this.riskFactorCount = riskFactorCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
    }

    public ScanStatus getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(ScanStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getRiskFactorCount() {
        return riskFactorCount;
    }

    public void setRiskFactorCount(int riskFactorCount) {
        this.riskFactorCount = riskFactorCount;
    }
}
