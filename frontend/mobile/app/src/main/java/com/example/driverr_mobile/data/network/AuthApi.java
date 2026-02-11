package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.LoginRequest;
import com.example.driverr_mobile.data.model.LoginResponse;
import com.example.driverr_mobile.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest request);
}
