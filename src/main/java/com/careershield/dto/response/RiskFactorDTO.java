package com.careershield.dto.response;

import com.careershield.enums.RiskCategory;
import com.careershield.enums.RiskLevel;

public class RiskFactorDTO {

    private Long id;
    private RiskCategory category;
    private String categoryLabel;
    private String title;
    private String description;
    private String whyItMatters;
    private RiskLevel severity;
    private int scorePenalty;

    public RiskFactorDTO() {
    }

    public RiskFactorDTO(Long id, RiskCategory category, String title, String description,
                         String whyItMatters, RiskLevel severity, int scorePenalty) {
        this.id = id;
        this.category = category;
        this.categoryLabel = category != null ? category.getLabel() : "General";
        this.title = title;
        this.description = description;
        this.whyItMatters = whyItMatters;
        this.severity = severity;
        this.scorePenalty = scorePenalty;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RiskCategory getCategory() {
        return category;
    }

    public void setCategory(RiskCategory category) {
        this.category = category;
        if (category != null) {
            this.categoryLabel = category.getLabel();
        }
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
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
