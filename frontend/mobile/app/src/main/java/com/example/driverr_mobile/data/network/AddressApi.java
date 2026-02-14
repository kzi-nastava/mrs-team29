package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.AddressResponse;
import com.example.driverr_mobile.data.model.GeocodeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressApi {

    @POST("addresses/geocode")
    Call<AddressResponse> geocodeAndSave(@Body GeocodeRequest request);

    @GET("addresses/{id}")
    Call<AddressResponse> getAddress(@Path("id") String id);
}
