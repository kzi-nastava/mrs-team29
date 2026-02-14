package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.DriverRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DriverApi {

    @POST("drivers/register")
    Call<ApiResponse<Object>> registerDriver(@Body DriverRegisterRequest request);
}
