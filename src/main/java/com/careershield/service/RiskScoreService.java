package com.careershield.service;

import com.careershield.dto.response.MLPredictionResponse;
import com.careershield.entity.JobScan;
import com.careershield.entity.RiskAssessment;
import com.careershield.entity.RiskFactor;
import com.careershield.enums.RiskLevel;
import com.careershield.enums.VerificationVerdict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service calculating final weighted risk and safety scores,
 * verdicts, and actionable student safety advice.
 * Demonstrates Java Business Logic, Streams API, and Math calculations.
 */
@Service
public class RiskScoreService {

    public RiskAssessment calculateAssessment(JobScan jobScan, List<RiskFactor> factors, MLPredictionResponse mlResponse) {
        // 1. Calculate cumulative penalty from heuristic rules using Java Streams
        int rulePenalties = factors.stream()
                .mapToInt(RiskFactor::getScorePenalty)
                .sum();

        // 2. Add weighted ML penalty (0 to 30 points)
        int mlPenalty = 0;
        if (mlResponse != null && mlResponse.isFake()) {
            mlPenalty = (int) Math.round(mlResponse.getFakeProbability() * 30.0);
        }

        int totalPenalty = rulePenalties + mlPenalty;

        // 3. Compute final Overall Safety Score (0 to 100)
        int rawScore = 100 - totalPenalty;
        int overallSafetyScore = Math.max(5, Math.min(98, rawScore));

        // 4. Map to RiskLevel
        RiskLevel riskLevel;
        VerificationVerdict verdict;

        if (overallSafetyScore >= 80) {
            riskLevel = RiskLevel.LOW;
            verdict = VerificationVerdict.GENUINE;
        } else if (overallSafetyScore >= 60) {
            riskLevel = RiskLevel.MEDIUM;
            verdict = VerificationVerdict.SUSPICIOUS;
        } else if (overallSafetyScore >= 35) {
            riskLevel = RiskLevel.HIGH;
            verdict = VerificationVerdict.HIGH_RISK_FRAUD;
        } else {
            riskLevel = RiskLevel.CRITICAL;
            verdict = VerificationVerdict.HIGH_RISK_FRAUD;
        }

        // 5. Generate comprehensive summary explanation
        String summaryExplanation = generateSummary(jobScan, factors, mlResponse, riskLevel, overallSafetyScore);

        // 6. Generate actionable safety recommendations
        String safetyRecommendation = generateRecommendations(factors, riskLevel);

        double ruleScore = Math.max(0.0, 100.0 - rulePenalties);

        RiskAssessment assessment = new RiskAssessment(
                jobScan,
                overallSafetyScore,
                riskLevel,
                verdict,
                mlResponse != null ? mlResponse.getConfidence() : 0.85,
                ruleScore,
                summaryExplanation,
                safetyRecommendation
        );

        // Link factors to assessment
        for (RiskFactor factor : factors) {
            assessment.addRiskFactor(factor);
        }

        return assessment;
    }

    private String generateSummary(JobScan jobScan, List<RiskFactor> factors,
                                   MLPredictionResponse ml, RiskLevel level, int score) {
        StringBuilder sb = new StringBuilder();
        sb.append("CareerShield AI analyzed this posting for '").append(jobScan.getJobTitle())
                .append("' at '").append(jobScan.getCompanyName()).append("'. ");

        if (level == RiskLevel.LOW) {
            sb.append("The posting demonstrates characteristics consistent with genuine corporate listings. ")
              .append("No critical red flags (upfront deposits or domain impersonation) were identified. ")
              .append("Overall safety index is evaluated at ").append(score).append("/100.");
        } else if (level == RiskLevel.MEDIUM) {
            sb.append("Detected ").append(factors.size()).append(" potential risk indicator(s). ")
              .append("While this opportunity may be genuine, certain details (e.g. unverified contact or vague requirements) ")
              .append("warrant independent verification. Overall safety score is ").append(score).append("/100.");
        } else if (level == RiskLevel.HIGH) {
            sb.append("HIGH RISK DETECTED: Found ").append(factors.size()).append(" serious red flag(s) ")
              .append("commonly associated with fraudulent employment listings. ")
              .append("The AI engine advises extreme caution before sharing personal credentials or resumes.");
        } else {
            sb.append("CRITICAL SCAM ALERT: This posting exhibits definitive characteristics of employment fraud (Score: ")
              .append(score).append("/100). Found ").append(factors.size()).append(" critical violation(s), ")
              .append("including upfront payment demands or domain spoofing. Do NOT engage with this poster.");
        }

        if (ml != null && ml.isFake()) {
            sb.append(" The AI NLP Model flagged the job description as suspicious with ")
              .append(Math.round(ml.getConfidence() * 100)).append("% confidence.");
        }

        return sb.toString();
    }

    private String generateRecommendations(List<RiskFactor> factors, RiskLevel level) {
        StringBuilder sb = new StringBuilder();

        sb.append("1. NEVER pay any registration, training, or laptop deposit fees under any circumstances.\n");
        sb.append("2. Verify this vacancy on the company's official careers portal (look for careers.company.com).\n");

        boolean hasDomainIssue = factors.stream().anyMatch(f -> f.getTitle().toLowerCase().contains("email"));
        if (hasDomainIssue) {
            sb.append("3. The recruiter used an unverified or personal email. Check LinkedIn to confirm their employment at the firm.\n");
        }

        boolean hasTelegram = factors.stream().anyMatch(f -> f.getTitle().toLowerCase().contains("channel") || f.getTitle().toLowerCase().contains("platform"));
        if (hasTelegram) {
            sb.append("4. Reject interviews conducted exclusively via Telegram or WhatsApp chats without a formal video conference or official invite.\n");
        }

        if (level == RiskLevel.CRITICAL || level == RiskLevel.HIGH) {
            sb.append("5. Do not send identity cards, Aadhaar/SSN numbers, or bank account details.\n");
            sb.append("6. Save this incident into your CareerShield Evidence Vault and notify your college placement cell.");
        } else {
            sb.append("5. Standard interview preparation recommended. Ensure you review the offer letter thoroughly before signing.");
        }

        return sb.toString();
    }
}
