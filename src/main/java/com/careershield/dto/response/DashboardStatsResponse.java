package com.careershield.dto.response;

public class DashboardStatsResponse {

    private long totalScans;
    private long safeScans;       // LOW risk
    private long suspiciousScans; // MEDIUM risk
    private long highRiskScans;   // HIGH + CRITICAL risk
    private long evidenceCount;
    private long savedJobsCount;
    private int averageSafetyScore;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalScans, long safeScans, long suspiciousScans,
                                  long highRiskScans, long evidenceCount, long savedJobsCount,
                                  int averageSafetyScore) {
        this.totalScans = totalScans;
        this.safeScans = safeScans;
        this.suspiciousScans = suspiciousScans;
        this.highRiskScans = highRiskScans;
        this.evidenceCount = evidenceCount;
        this.savedJobsCount = savedJobsCount;
        this.averageSafetyScore = averageSafetyScore;
    }

    // Getters and Setters
    public long getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(long totalScans) {
        this.totalScans = totalScans;
    }

    public long getSafeScans() {
        return safeScans;
    }

    public void setSafeScans(long safeScans) {
        this.safeScans = safeScans;
    }

    public long getSuspiciousScans() {
        return suspiciousScans;
    }

    public void setSuspiciousScans(long suspiciousScans) {
        this.suspiciousScans = suspiciousScans;
    }

    public long getHighRiskScans() {
        return highRiskScans;
    }

    public void setHighRiskScans(long highRiskScans) {
        this.highRiskScans = highRiskScans;
    }

    public long getEvidenceCount() {
        return evidenceCount;
    }

    public void setEvidenceCount(long evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public long getSavedJobsCount() {
        return savedJobsCount;
    }

    public void setSavedJobsCount(long savedJobsCount) {
        this.savedJobsCount = savedJobsCount;
    }

    public int getAverageSafetyScore() {
        return averageSafetyScore;
    }

    public void setAverageSafetyScore(int averageSafetyScore) {
        this.averageSafetyScore = averageSafetyScore;
    }
}
