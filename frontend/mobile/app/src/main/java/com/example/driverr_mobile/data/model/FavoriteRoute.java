package com.example.driverr_mobile.data.model;

import java.util.List;

public class FavoriteRoute {
    private String id;
    private String name;
    private String userId;
    private String pickupAddressId;
    private String destinationAddressId;
    private List<String> stopAddressIds;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUserId() { return userId; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public List<String> getStopAddressIds() { return stopAddressIds; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setPickupAddressId(String pickupAddressId) { this.pickupAddressId = pickupAddressId; }
    public void setDestinationAddressId(String destinationAddressId) { this.destinationAddressId = destinationAddressId; }
    public void setStopAddressIds(List<String> stopAddressIds) { this.stopAddressIds = stopAddressIds; }
}
