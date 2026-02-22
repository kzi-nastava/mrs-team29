package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.DriverActivationRequest;
import com.example.driverr_mobile.data.model.DriverRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DriverApi {

    @POST("drivers/register")
    Call<ApiResponse<Object>> registerDriver(@Body DriverRegisterRequest request);

    @POST("drivers/activate")
    Call<ApiResponse<Object>> activateDriver(@Body DriverActivationRequest request);

    @GET("drivers/{driverId}/working-hours")
    Call<Double> getWorkingHours(@Path("driverId") String driverId);
}
