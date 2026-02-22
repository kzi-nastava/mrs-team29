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
    private static UserApi userApi;
    private static AdminApi adminApi;
    private static RideApi rideApi;
    private static ReportApi reportApi;
    private static AddressApi addressApi;
    private static FavoriteRouteApi favoriteRouteApi;

    private ApiClient() {}

    public static Retrofit getInstance() {
        return getRetrofit();
    }

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

    public static UserApi getUserApi() {
        if (userApi == null) {
            userApi = getRetrofit().create(UserApi.class);
        }
        return userApi;
    }

    public static AdminApi getAdminApi() {
        if (adminApi == null) {
            adminApi = getRetrofit().create(AdminApi.class);
        }
        return adminApi;
    }

    public static RideApi getRideApi() {
        if (rideApi == null) {
            rideApi = getRetrofit().create(RideApi.class);
        }
        return rideApi;
    }

    public static ReportApi getReportApi() {
        if (reportApi == null) {
            reportApi = getRetrofit().create(ReportApi.class);
        }
        return reportApi;
    }

    public static AddressApi getAddressApi() {
        if (addressApi == null) {
            addressApi = getRetrofit().create(AddressApi.class);
        }
        return addressApi;
    }

    public static FavoriteRouteApi getFavoriteRouteApi() {
        if (favoriteRouteApi == null) {
            favoriteRouteApi = getRetrofit().create(FavoriteRouteApi.class);
        }
        return favoriteRouteApi;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthTokenInterceptor())
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
