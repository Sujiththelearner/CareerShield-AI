package com.careershield.service;

import com.careershield.dto.response.MLPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

/**
 * Service managing communication with the Python Machine Learning API.
 * Features an internal Java NLP Heuristic Fallback Engine so the system operates
 * smoothly whether the Python sidecar is running or not.
 * Demonstrates Java Exception Handling, Resilience, and NLP heuristics.
 */
@Service
public class MLPredictionService {

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;

    public MLPredictionService(RestTemplateBuilder restTemplateBuilder,
                               @Value("${careershield.ml.service-url:http://localhost:8000/predict}") String mlServiceUrl) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(1500))
                .setReadTimeout(Duration.ofMillis(2000))
                .build();
        this.mlServiceUrl = mlServiceUrl;
    }

    /**
     * Sends the job text to the ML microservice, or falls back to the Java NLP engine.
     */
    public MLPredictionResponse predict(String jobText) {
        if (jobText == null || jobText.trim().isEmpty()) {
            return new MLPredictionResponse(false, 0.5, 0.0, "Java NLP Engine", "Empty text provided");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> payload = Collections.singletonMap("text", jobText);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(mlServiceUrl, requestEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                boolean isFake = Boolean.TRUE.equals(body.get("is_fake"));
                double confidence = body.get("confidence") instanceof Number
                        ? ((Number) body.get("confidence")).doubleValue()
                        : 0.85;
                double prob = body.get("fake_probability") instanceof Number
                        ? ((Number) body.get("fake_probability")).doubleValue()
                        : (isFake ? confidence : 1.0 - confidence);

                return new MLPredictionResponse(isFake, confidence, prob, "Python FastAPI LightGBM/TF-IDF Model", "Live ML prediction");
            }
        } catch (Exception ex) {
            // Python service is not running or unreachable -> Fall back gracefully to Java NLP
            System.out.println("ℹ️  Python ML Service not reached (" + ex.getMessage() + "). Activating built-in Java NLP Fallback Engine.");
        }

        return evaluateWithJavaNlpEngine(jobText);
    }

    /**
     * Built-in Java NLP Scam Classification Engine.
     * Evaluates linguistic features, scam lexical frequency, and standard hiring syntax.
     */
    private MLPredictionResponse evaluateWithJavaNlpEngine(String text) {
        String lower = text.toLowerCase();

        String[] scamLexicon = {
                "wire transfer", "western union", "cash app", "crypto wallet", "bitcoin",
                "upfront fee", "training charge", "registration charge", "data entry clerk daily",
                "earn money fast", "telegram chat", "package forwarding", "mystery shopper",
                "re-shipping", "no experience required $1000", "task reward", "whatsapp hr"
        };

        String[] legitimateHiringLexicon = {
                "bachelor", "master", "qualifications", "responsibilities", "collaborate",
                "team", "software", "engineering", "curriculum", "benefits", "equal opportunity",
                "git", "java", "development", "internship program", "mentorship", "full-time"
        };

        int scamSignals = 0;
        for (String term : scamLexicon) {
            if (lower.contains(term)) {
                scamSignals++;
            }
        }

        int legitSignals = 0;
        for (String term : legitimateHiringLexicon) {
            if (lower.contains(term)) {
                legitSignals++;
            }
        }

        double fakeProbability;
        if (scamSignals > 0) {
            fakeProbability = Math.min(0.98, 0.50 + (scamSignals * 0.15) - (legitSignals * 0.05));
        } else if (legitSignals >= 3) {
            fakeProbability = Math.max(0.05, 0.20 - (legitSignals * 0.03));
        } else {
            fakeProbability = 0.35; // Neutral
        }

        boolean isFake = fakeProbability >= 0.55;
        double confidence = Math.abs(fakeProbability - 0.50) * 2.0; // 0.0 to 1.0
        confidence = Math.max(0.65, Math.min(0.95, confidence + 0.30));

        return new MLPredictionResponse(
                isFake,
                Math.round(confidence * 100.0) / 100.0,
                Math.round(fakeProbability * 100.0) / 100.0,
                "Java NLP Scam Engine (Resilient Embedded Fallback)",
                "Classified using linguistic density and semantic keyword distribution."
        );
    }
}
