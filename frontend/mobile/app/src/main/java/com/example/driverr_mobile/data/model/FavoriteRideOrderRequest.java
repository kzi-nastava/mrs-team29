package com.example.driverr_mobile.data.model;

public class FavoriteRideOrderRequest {
    private String clientId;

    public FavoriteRideOrderRequest() {}

    public FavoriteRideOrderRequest(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() { return clientId; }
}
