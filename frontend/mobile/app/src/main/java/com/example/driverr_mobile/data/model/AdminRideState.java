package com.example.driverr_mobile.data.model;

public class AdminRideState {
    private String rideId;
    private String driverId;
    private String driverName;
    private String status;

    private String pickupAddress;
    private String destinationAddress;

    private String scheduledTime;
    private String startedAt;
    private String estimatedArrival;

    private double currentLatitude;
    private double currentLongitude;
    private String currentLocationDescription;

    public String getRideId() { return rideId; }
    public String getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getStatus() { return status; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDestinationAddress() { return destinationAddress; }
    public String getScheduledTime() { return scheduledTime; }
    public String getStartedAt() { return startedAt; }
    public String getEstimatedArrival() { return estimatedArrival; }
    public double getCurrentLatitude() { return currentLatitude; }
    public double getCurrentLongitude() { return currentLongitude; }
    public String getCurrentLocationDescription() { return currentLocationDescription; }
}
