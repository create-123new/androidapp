package com.example.aidsappdetection.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FastApiService {
    @POST("/predict/")
    Call<PredictionResult> getPrediction(@Body PredictionRequest request);
}

