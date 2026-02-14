package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.AddressResponse;
import com.example.driverr_mobile.data.model.GeocodeRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddressApi {

    @POST("addresses/geocode")
    Call<AddressResponse> geocodeAndSave(@Body GeocodeRequest request);
}
