package com.example.driverr_mobile.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RideResponse {
    @SerializedName("rideId")
    private String rideId;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("driverId")
    private String driverId;
    
    @SerializedName("driverName")
    private String driverName;
    
    @SerializedName("pickupAddressId")
    private String pickupAddressId;
    
    @SerializedName("pickupAddress")
    private String pickupAddress;
    
    @SerializedName("destinationAddressId")
    private String destinationAddressId;
    
    @SerializedName("destinationAddress")
    private String destinationAddress;
    
    @SerializedName("stopAddressIds")
    private List<String> stopAddressIds;
    
    @SerializedName("passengerIds")
    private List<String> passengerIds;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("startedAt")
    private String startedAt;
    
    @SerializedName("finishedAt")
    private String finishedAt;
    
    @SerializedName("scheduledTime")
    private String scheduledTime;

    // Getters
    public String getRideId() { return rideId; }
    public String getStatus() { return status; }
    public double getPrice() { return price; }
    public String getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public String getDestinationAddress() { return destinationAddress; }
    public List<String> getStopAddressIds() { return stopAddressIds; }
    public List<String> getPassengerIds() { return passengerIds; }
    public String getCreatedAt() { return createdAt; }
    public String getStartedAt() { return startedAt; }
    public String getFinishedAt() { return finishedAt; }
    public String getScheduledTime() { return scheduledTime; }

    // Setters
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setStatus(String status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setPickupAddressId(String pickupAddressId) { this.pickupAddressId = pickupAddressId; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDestinationAddressId(String destinationAddressId) { this.destinationAddressId = destinationAddressId; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    public void setStopAddressIds(List<String> stopAddressIds) { this.stopAddressIds = stopAddressIds; }
    public void setPassengerIds(List<String> passengerIds) { this.passengerIds = passengerIds; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }
    public void setFinishedAt(String finishedAt) { this.finishedAt = finishedAt; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
}
