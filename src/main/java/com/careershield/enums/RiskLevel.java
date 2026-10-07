package com.careershield.enums;

/**
 * Categorization of risk severity for job scans and specific warning factors.
 */
public enum RiskLevel {
    LOW("Safe / Low Risk", "badge-safe"),
    MEDIUM("Caution / Moderate Risk", "badge-warning"),
    HIGH("High Risk / Suspicious", "badge-danger"),
    CRITICAL("Critical Threat / Likely Fraud", "badge-critical");

    private final String displayName;
    private final String badgeClass;

    RiskLevel(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
