package com.careershield.enums;

/**
 * Categorization of different fake internship / job scam indicators.
 */
public enum RiskCategory {
    COMPANY_IDENTITY("Company Legitimacy & Identity"),
    EMAIL_DOMAIN("Recruiter Email & Domain Mismatch"),
    SALARY_ANOMALY("Compensation & Stipend Outlier"),
    PAYMENT_REQUEST("Upfront Fee or Deposit Demand"),
    URGENCY_LANGUAGE("High-Pressure & Urgency Tactics"),
    INTERVIEW_PROCESS("Informal / Suspicious Interview Channel"),
    JOB_DESCRIPTION("Vague Description & Grammatical Flags");

    private final String label;

    RiskCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
