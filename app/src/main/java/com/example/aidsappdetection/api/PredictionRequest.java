package com.example.aidsappdetection.api;

public class PredictionRequest {
    private float cd40;
    private float cd420;
    private float cd80;
    private float cd820;

    public PredictionRequest(float cd40, float cd420, float cd80, float cd820) {
        this.cd40 = cd40;
        this.cd420 = cd420;
        this.cd80 = cd80;
        this.cd820 = cd820;
    }

    // Getters & setters if needed
}
