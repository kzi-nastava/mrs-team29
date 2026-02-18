package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.ProfileChangeRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminApi {

    @GET("admin/profile-change-requests")
    Call<List<ProfileChangeRequest>> getPendingProfileChangeRequests();

    @POST("admin/profile-change-requests/{requestId}/approve")
    Call<ApiResponse<String>> approveProfileChangeRequest(@Path("requestId") String requestId);

    @POST("admin/profile-change-requests/{requestId}/reject")
    Call<ApiResponse<String>> rejectProfileChangeRequest(@Path("requestId") String requestId);
}
