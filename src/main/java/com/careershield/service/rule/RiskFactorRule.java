package com.careershield.service.rule;

import com.careershield.entity.JobScan;
import com.careershield.entity.RiskFactor;
import com.careershield.enums.RiskCategory;

import java.util.Optional;

/**
 * Interface defining the contract for modular Scam Detection Rules.
 * Demonstrates the Strategy Pattern, Abstraction, and Polymorphism in Java.
 */
public interface RiskFactorRule {

    /**
     * Evaluates a job posting scan against this specific rule's heuristic logic.
     * @param jobScan the job scan details provided by the student
     * @return Optional containing RiskFactor if a red flag was identified, or empty if clean
     */
    Optional<RiskFactor> evaluate(JobScan jobScan);

    /**
     * Returns the functional risk category.
     */
    RiskCategory getCategory();

    /**
     * Priority order for rule execution.
     */
    int getPriority();
}
