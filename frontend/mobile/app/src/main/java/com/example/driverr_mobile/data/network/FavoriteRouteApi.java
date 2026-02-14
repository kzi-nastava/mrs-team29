package com.example.driverr_mobile.data.network;

import com.example.driverr_mobile.data.model.FavoriteRideOrderRequest;
import com.example.driverr_mobile.data.model.FavoriteRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FavoriteRouteApi {

    @GET("favorite-routes/user/{userId}")
    Call<List<FavoriteRoute>> getMyFavorites(@Path("userId") String userId);

    @POST("favorite-routes")
    Call<FavoriteRoute> createFavorite(@Body FavoriteRoute route);

    @PUT("favorite-routes/{routeId}")
    Call<FavoriteRoute> updateFavorite(@Path("routeId") String routeId, @Body FavoriteRoute route);

    @DELETE("favorite-routes/{routeId}")
    Call<Object> deleteFavorite(@Path("routeId") String routeId);

    @POST("rides/favorites/{routeId}")
    Call<Object> orderFromFavorite(@Path("routeId") String routeId, @Body FavoriteRideOrderRequest request);
}
