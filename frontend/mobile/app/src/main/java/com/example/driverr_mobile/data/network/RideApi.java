package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.RideOrderRequest;
import com.example.driverr_mobile.data.model.RideResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideApi {

    @POST("rides")
    Call<RideResponse> orderRide(@Body RideOrderRequest request);

    @GET("rides/user/{userId}/active")
    Call<Boolean> hasActiveRide(@Path("userId") String userId);
}
