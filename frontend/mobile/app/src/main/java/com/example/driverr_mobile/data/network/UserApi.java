package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.ChangePasswordRequest;
import com.example.driverr_mobile.data.model.UpdateUserProfileRequest;
import com.example.driverr_mobile.data.model.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    @GET("users/{id}")
    Call<UserProfile> getProfile(@Path("id") String userId);

    @PUT("users/{id}")
    Call<UserProfile> updateProfile(@Path("id") String userId, @Body UpdateUserProfileRequest request);

    @POST("users/{id}/change-password")
    Call<Object> changePassword(@Path("id") String userId, @Body ChangePasswordRequest request);
}
