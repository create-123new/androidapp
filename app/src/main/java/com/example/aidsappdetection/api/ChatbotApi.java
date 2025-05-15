package com.example.aidsappdetection.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatbotApi {
    @Headers("Content-Type: application/json")
    @POST("/chat")
    Call<ChatResponse> getChatResponse(@Body ChatRequest request);
}