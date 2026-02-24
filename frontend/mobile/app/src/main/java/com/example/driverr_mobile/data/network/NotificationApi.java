package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.NotificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {

    @GET("notifications/user/{userId}")
    Call<List<NotificationResponse>> getUserNotifications(@Path("userId") String userId);

    @POST("notifications/{notificationId}/read")
    Call<Void> markAsRead(@Path("notificationId") String notificationId);
}
