package com.example.aidsappdetection.api;

import com.google.gson.annotations.SerializedName;


public class PredictionResult {
    // Change to String to match the expected response from the API
    @SerializedName("HIVStage")
    private String HIVStage;

    @SerializedName("healthStatus")
    private String healthStatus;

    public String getHIVStage() {
        return HIVStage;
    }

    public String getHealthStatus() {
        return healthStatus;
    }
}


