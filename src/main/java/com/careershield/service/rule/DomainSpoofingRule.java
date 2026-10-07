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
 * Detection rule checking for corporate impersonation using generic free webmail domains.
 */
@Component
public class DomainSpoofingRule implements RiskFactorRule {

    private static final List<String> FREE_WEBMAIL_DOMAINS = Arrays.asList(
            "@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com", "@rediffmail.com",
            "@mail.com", "@yopmail.com", "@proton.me", "@zoho.com"
    );

    private static final List<String> ENTERPRISE_COMPANIES = Arrays.asList(
            "google", "amazon", "microsoft", "apple", "meta", "netflix", "ibm", "cisco",
            "tcs", "tata consultancy", "infosys", "wipro", "accenture", "cognizant",
            "deloitte", "kpmg", "pwc", "ey", "ernst & young", "capgemini", "oracle", "salesforce"
    );

    @Override
    public Optional<RiskFactor> evaluate(JobScan jobScan) {
        String email = jobScan.getRecruiterEmail() != null ? jobScan.getRecruiterEmail().trim().toLowerCase() : "";
        String company = jobScan.getCompanyName() != null ? jobScan.getCompanyName().trim().toLowerCase() : "";

        if (email.isEmpty()) {
            return Optional.empty();
        }

        boolean isFreeEmail = FREE_WEBMAIL_DOMAINS.stream().anyMatch(email::endsWith);
        boolean isClaimedEnterprise = ENTERPRISE_COMPANIES.stream().anyMatch(company::contains);

        if (isFreeEmail && isClaimedEnterprise) {
            RiskFactor factor = new RiskFactor(
                    null,
                    getCategory(),
                    "Corporate Impersonation via Free Email Domain (" + email + ")",
                    "The recruiter claims to represent '" + jobScan.getCompanyName() + "' but is using a free personal email (" + email + ") rather than an official corporate domain.",
                    "Reputable enterprises, MNCs, and established startups communicate exclusively via verified corporate email domains (e.g., name@google.com or name@infosys.com). Scammers frequently use free webmail to impersonate famous brands.",
                    RiskLevel.CRITICAL,
                    35 // Deduct 35 safety points
            );
            return Optional.of(factor);
        } else if (isFreeEmail && !company.isEmpty()) {
            RiskFactor factor = new RiskFactor(
                    null,
                    getCategory(),
                    "Unverified Free Webmail Recruiter Address (" + email + ")",
                    "The recruiter provided a free webmail address (" + email + ") rather than an official company domain email.",
                    "While small indie creators or very young micro-startups occasionally use free webmail, established businesses maintain custom domain emails. Please verify this recruiter's identity through LinkedIn or the company's verified website.",
                    RiskLevel.MEDIUM,
                    15 // Deduct 15 safety points
            );
            return Optional.of(factor);
        }

        return Optional.empty();
    }

    @Override
    public RiskCategory getCategory() {
        return RiskCategory.EMAIL_DOMAIN;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
