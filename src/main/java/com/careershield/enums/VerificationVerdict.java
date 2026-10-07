package com.careershield.enums;

/**
 * Human-readable final verdict of the explainable safety assessment.
 */
public enum VerificationVerdict {
    GENUINE("Likely Genuine Opportunity"),
    SUSPICIOUS("Suspicious Indicators Detected - Exercise Caution"),
    HIGH_RISK_FRAUD("High-Risk Fraud / Scam Alert");

    private final String description;

    VerificationVerdict(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
