package com.example.aidsappdetection;

import android.util.Log;

import com.example.aidsappdetection.api.FastApiService;
import com.example.aidsappdetection.api.PredictionRequest;
import com.example.aidsappdetection.api.PredictionResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;


public class FastApiClient {

    public interface ApiCallback {
        void onSuccess(PredictionResult result);
        void onError(Throwable t);
    }

    public static void getPrediction(float cd40, float cd420, float cd80, float cd820, ApiCallback callback) {
        // Prepare request object
        PredictionRequest request = new PredictionRequest(cd40, cd420, cd80, cd820);

        // Get Retrofit service instance
        FastApiService service = RetrofitClient.getFastApiService();

        // Enqueue the POST request
        service.getPrediction(request).enqueue(new retrofit2.Callback<PredictionResult>() {
            @Override
            public void onResponse(Call<PredictionResult> call, Response<PredictionResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("API error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<PredictionResult> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}


