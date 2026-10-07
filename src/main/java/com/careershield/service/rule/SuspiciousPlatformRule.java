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
 * Detection rule checking for informal or unmonitored interview channels (Telegram, WhatsApp).
 */
@Component
public class SuspiciousPlatformRule implements RiskFactorRule {

    private static final List<String> SUSPICIOUS_CHANNELS = Arrays.asList(
            "telegram", "whatsapp message", "text on whatsapp", "ping on whatsapp",
            "contact via telegram", "telegram id", "signal chat", "wire app", "skype interview only"
    );

    @Override
    public Optional<RiskFactor> evaluate(JobScan jobScan) {
        String content = (jobScan.getJobDescription() + " " + jobScan.getSalaryInfo()).toLowerCase();

        for (String channel : SUSPICIOUS_CHANNELS) {
            if (content.contains(channel)) {
                RiskFactor factor = new RiskFactor(
                        null,
                        getCategory(),
                        "Informal Communication / Untraceable Channel (" + channel + ")",
                        "The posting directs candidates to conduct the interview or onboarding via '" + channel + "'.",
                        "Legitimate corporate hiring relies on enterprise communication tools (Google Meet, Microsoft Teams, Zoom, Greenhouse) linked to corporate scheduling calendars. Cybercriminals direct victims to Telegram and WhatsApp to avoid corporate oversight, account tracking, and legal law enforcement.",
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
        return RiskCategory.INTERVIEW_PROCESS;
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
