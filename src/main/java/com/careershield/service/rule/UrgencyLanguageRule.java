package com.careershield.service.rule;

import com.careershield.entity.JobScan;
import com.careershield.entity.RiskFactor;
import com.careershield.enums.RiskCategory;
import com.careershield.enums.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Detection rule checking for artificial urgency, FOMO tactics, or offers without assessment.
 */
@Component
public class UrgencyLanguageRule implements RiskFactorRule {

    private static final List<String> URGENCY_TRIGGERS = Arrays.asList(
            "immediate hiring today", "offer letter in 1 hour", "no interview required",
            "direct selection without interview", "limited slots only", "urgent hiring 100 vacancies",
            "spot offer", "start immediately today", "no resume required", "guaranteed placement today"
    );

    @Override
    public Optional<RiskFactor> evaluate(JobScan jobScan) {
        String text = jobScan.getJobDescription().toLowerCase();

        for (String trigger : URGENCY_TRIGGERS) {
            if (text.contains(trigger)) {
                RiskFactor factor = new RiskFactor(
                        null,
                        getCategory(),
                        "High-Pressure Urgency Tactics (" + trigger + ")",
                        "The posting promises '" + trigger + "', bypassing normal technical vetting or standard interview procedures.",
                        "Legitimate tech firms and corporations vet interns through technical rounds, portfolio reviews, or behavioral interviews. Bypassing candidate vetting is a known tactic to compel students to sign agreements or pay deposits before thinking critically.",
                        RiskLevel.MEDIUM,
                        15 // Deduct 15 safety points
                );
                return Optional.of(factor);
            }
        }

        return Optional.empty();
    }

    @Override
    public RiskCategory getCategory() {
        return RiskCategory.URGENCY_LANGUAGE;
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
