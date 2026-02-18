package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.RideOrderRequest;
import com.example.driverr_mobile.data.model.RideResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideApi {

    @POST("rides")
    Call<RideResponse> orderRide(@Body RideOrderRequest request);

    @GET("rides/user/{userId}/active")
    Call<Boolean> hasActiveRide(@Path("userId") String userId);

    @GET("rides/user/{userId}/history")
    Call<List<RideResponse>> getUserRideHistory(@Path("userId") String userId);

    // ============ DRIVER ENDPOINTS ============
    
    @GET("rides/driver/{driverId}/current")
    Call<RideResponse> getDriverCurrentRide(@Path("driverId") String driverId);

    @POST("rides/{rideId}/start")
    Call<RideResponse> startRide(@Path("rideId") String rideId, @Query("driverId") String driverId);

    @POST("rides/{rideId}/finish")
    Call<RideResponse> finishRide(@Path("rideId") String rideId, @Query("driverId") String driverId);

    @GET("rides/driver/{driverId}/history")
    Call<List<RideResponse>> getDriverRideHistory(
        @Path("driverId") String driverId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );
}
