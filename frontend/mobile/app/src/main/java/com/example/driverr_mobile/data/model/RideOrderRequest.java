package com.example.driverr_mobile.data.model;

import java.util.List;

public class RideOrderRequest {
    private String creatorId;
    private String pickupAddressId;
    private String destinationAddressId;
    private List<String> stopAddressIds;
    private List<String> passengerIds;
    private String vehicleType;
    private boolean pets;
    private boolean baby;
    private String scheduledTime;
    private String notes;

    public RideOrderRequest() {}

    public RideOrderRequest(
            String creatorId,
            String pickupAddressId,
            String destinationAddressId,
            List<String> stopAddressIds,
            List<String> passengerIds,
            String vehicleType,
            boolean pets,
            boolean baby,
            String scheduledTime,
            String notes
    ) {
        this.creatorId = creatorId;
        this.pickupAddressId = pickupAddressId;
        this.destinationAddressId = destinationAddressId;
        this.stopAddressIds = stopAddressIds;
        this.passengerIds = passengerIds;
        this.vehicleType = vehicleType;
        this.pets = pets;
        this.baby = baby;
        this.scheduledTime = scheduledTime;
        this.notes = notes;
    }

    public String getCreatorId() { return creatorId; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public List<String> getStopAddressIds() { return stopAddressIds; }
    public List<String> getPassengerIds() { return passengerIds; }
    public String getVehicleType() { return vehicleType; }
    public boolean isPets() { return pets; }
    public boolean isBaby() { return baby; }
    public String getScheduledTime() { return scheduledTime; }
    public String getNotes() { return notes; }
}
