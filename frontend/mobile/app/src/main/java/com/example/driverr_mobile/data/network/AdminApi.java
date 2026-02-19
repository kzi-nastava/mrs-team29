package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.ApiResponse;
import com.example.driverr_mobile.data.model.BlockUserRequest;
import com.example.driverr_mobile.data.model.ProfileChangeRequest;
import com.example.driverr_mobile.data.model.UserBlockStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @GET("admin/users/block-status")
    Call<List<UserBlockStatus>> getAllUsersBlockStatus();

    @GET("admin/users/{userId}/block-status")
    Call<UserBlockStatus> getUserBlockStatus(@Path("userId") String userId);

    @POST("admin/users/{userId}/block")
    Call<ApiResponse<String>> blockUser(@Path("userId") String userId, @Body BlockUserRequest request);

    @POST("admin/users/{userId}/unblock")
    Call<ApiResponse<String>> unblockUser(@Path("userId") String userId);
}
