package com.example.aidsappdetection.api;

public class PredictRequest {
    private float cd40;
    private float cd420;
    private float cd80;
    private float cd820;

    public PredictRequest(float cd40, float cd420, float cd80, float cd820) {
        this.cd40 = cd40;
        this.cd420 = cd420;
        this.cd80 = cd80;
        this.cd820 = cd820;
    }

    // Getters and setters
    public float getCd40() { return cd40; }
    public void setCd40(float cd40) { this.cd40 = cd40; }

    public float getCd420() { return cd420; }
    public void setCd420(float cd420) { this.cd420 = cd420; }

    public float getCd80() { return cd80; }
    public void setCd80(float cd80) { this.cd80 = cd80; }

    public float getCd820() { return cd820; }
    public void setCd820(float cd820) { this.cd820 = cd820; }
}
