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
 * Detection rule checking for unrealistic salary promises for entry-level or minimal skill work.
 */
@Component
public class SalaryAnomalyRule implements RiskFactorRule {

    private static final List<String> SALARY_ANOMALY_TRIGGERS = Arrays.asList(
            "earn daily", "earn 5000 daily", "earn 1000 daily", "daily payout", "weekly 50000",
            "$500/day", "$1000/day", "earn from home without experience", "make $100 per hour",
            "no skills required high pay", "guaranteed income daily", "instant payout daily"
    );

    @Override
    public Optional<RiskFactor> evaluate(JobScan jobScan) {
        String text = (jobScan.getJobTitle() + " " + jobScan.getSalaryInfo() + " " + jobScan.getJobDescription()).toLowerCase();

        for (String trigger : SALARY_ANOMALY_TRIGGERS) {
            if (text.contains(trigger)) {
                RiskFactor factor = new RiskFactor(
                        null,
                        getCategory(),
                        "Unrealistic Compensation Anomaly (" + trigger + ")",
                        "The posting advertises compensation packages ('" + trigger + "') that drastically exceed real-world market rates for entry-level or student positions.",
                        "Scammers use inflated earnings and daily payout promises as clickbait to lure students into task-based financial scams (e.g. YouTube video liking scams, rating fake products, or money laundering).",
                        RiskLevel.HIGH,
                        25 // Deduct 25 safety points
                );
                return Optional.of(factor);
            }
        }

        return Optional.empty();
    }

    @Override
    public RiskCategory getCategory() {
        return RiskCategory.SALARY_ANOMALY;
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
