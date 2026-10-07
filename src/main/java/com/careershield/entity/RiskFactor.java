package com.careershield.entity;

import com.careershield.enums.RiskCategory;
import com.careershield.enums.RiskLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity capturing a specific detected red flag or suspicious indicator.
 * Provides the explainability that makes CareerShield AI actionable for students.
 */
@Entity
@Table(name = "risk_factors", indexes = {
    @Index(name = "idx_factor_assessment", columnList = "risk_assessment_id"),
    @Index(name = "idx_factor_category", columnList = "category")
})
public class RiskFactor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_assessment_id", nullable = false)
    @JsonIgnore
    private RiskAssessment riskAssessment;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private RiskCategory category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "LONGTEXT")
    private String description; // What was detected

    @Lob
    @Column(name = "why_it_matters", nullable = false, columnDefinition = "LONGTEXT")
    private String whyItMatters; // Why this represents a scam threat

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private RiskLevel severity;

    @Column(name = "score_penalty", nullable = false)
    private int scorePenalty; // Number of points deducted from safety score

    public RiskFactor() {
    }

    public RiskFactor(RiskAssessment riskAssessment, RiskCategory category, String title,
                      String description, String whyItMatters, RiskLevel severity, int scorePenalty) {
        this.riskAssessment = riskAssessment;
        this.category = category;
        this.title = title;
        this.description = description;
        this.whyItMatters = whyItMatters;
        this.severity = severity;
        this.scorePenalty = scorePenalty;
    }

    // Getters and Setters
    public RiskAssessment getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(RiskAssessment riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public RiskCategory getCategory() {
        return category;
    }

    public void setCategory(RiskCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public RiskLevel getSeverity() {
        return severity;
    }

    public void setSeverity(RiskLevel severity) {
        this.severity = severity;
    }

    public int getScorePenalty() {
        return scorePenalty;
    }

    public void setScorePenalty(int scorePenalty) {
        this.scorePenalty = scorePenalty;
    }
}
