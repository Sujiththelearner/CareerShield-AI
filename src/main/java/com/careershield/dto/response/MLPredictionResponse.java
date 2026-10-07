package com.careershield.dto.response;

public class MLPredictionResponse {

    private boolean fake;
    private double confidence; // e.g. 0.85
    private double fakeProbability; // 0.0 to 1.0
    private String modelName;
    private String note;

    public MLPredictionResponse() {
    }

    public MLPredictionResponse(boolean fake, double confidence, double fakeProbability, String modelName, String note) {
        this.fake = fake;
        this.confidence = confidence;
        this.fakeProbability = fakeProbability;
        this.modelName = modelName;
        this.note = note;
    }

    public boolean isFake() {
        return fake;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getFakeProbability() {
        return fakeProbability;
    }

    public void setFakeProbability(double fakeProbability) {
        this.fakeProbability = fakeProbability;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
