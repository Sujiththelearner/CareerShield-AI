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
 * Detection rule checking for upfront monetary demands (training fee, laptop deposit, registration fee).
 */
@Component
public class PaymentDemandRule implements RiskFactorRule {

    private static final List<String> PAYMENT_INDICATORS = Arrays.asList(
            "registration fee", "training fee", "security deposit", "laptop deposit",
            "courier charge", "processing fee", "refundable deposit", "pay before offer",
            "send money", "bank transfer before", "upi payment", "crypto deposit",
            "buy equipment", "pay for background check", "id card fee", "application charge"
    );

    @Override
    public Optional<RiskFactor> evaluate(JobScan jobScan) {
        String content = (jobScan.getJobDescription() + " " + jobScan.getSalaryInfo()).toLowerCase();

        for (String indicator : PAYMENT_INDICATORS) {
            if (content.contains(indicator)) {
                RiskFactor factor = new RiskFactor(
                        null,
                        getCategory(),
                        "Upfront Payment or Deposit Demanded (" + indicator + ")",
                        "The posting mentions '" + indicator + "'. Scammers frequently pose as employers demanding money under the guise of onboarding, documentation, or hardware delivery.",
                        "Legitimate employers NEVER ask candidates to pay for training, equipment, interview scheduling, or application fees. This is the #1 indicator of employment fraud.",
                        RiskLevel.CRITICAL,
                        40 // Deduct 40 safety points
                );
                return Optional.of(factor);
            }
        }

        return Optional.empty();
    }

    @Override
    public RiskCategory getCategory() {
        return RiskCategory.PAYMENT_REQUEST;
    }

    @Override
    public int getPriority() {
        return 1; // Highest priority
    }
}
