package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.RatingRequest;
import com.example.driverr_mobile.data.model.RatingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RatingApi {

    @POST("ratings")
    Call<RatingResponse> submitRating(@Body RatingRequest request);
}
