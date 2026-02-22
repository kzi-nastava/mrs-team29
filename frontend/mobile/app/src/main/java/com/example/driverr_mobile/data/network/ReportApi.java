package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.RideReportResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportApi {

    @GET("reports/user/{userId}")
    Call<RideReportResponse> getUserReport(
            @Path("userId") String userId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("reports/admin")
    Call<RideReportResponse> getAdminReport(
            @Query("scope") String scope,
            @Query("userEmail") String userEmail,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
}
