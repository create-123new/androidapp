package com.example.aidsappdetection.api;

public class PredictResponse {
    private int stageLabel;
    private int healthStatusLabel;

    public int getStageLabel() { return stageLabel; }
    public void setStageLabel(int stageLabel) { this.stageLabel = stageLabel; }

    public int getHealthStatusLabel() { return healthStatusLabel; }
    public void setHealthStatusLabel(int healthStatusLabel) { this.healthStatusLabel = healthStatusLabel; }
}

