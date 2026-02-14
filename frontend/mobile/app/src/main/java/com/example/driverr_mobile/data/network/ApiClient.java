package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static Retrofit retrofit;
    private static AuthApi authApi;
    private static DriverApi driverApi;

    private ApiClient() {}

    public static AuthApi getAuthApi() {
        if (authApi == null) {
            authApi = getRetrofit().create(AuthApi.class);
        }
        return authApi;
    }

    public static DriverApi getDriverApi() {
        if (driverApi == null) {
            driverApi = getRetrofit().create(DriverApi.class);
        }
        return driverApi;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
