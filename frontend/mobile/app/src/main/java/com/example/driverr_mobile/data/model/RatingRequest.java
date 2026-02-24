package com.example.driverr_mobile.data.model;

public class RatingRequest {
    private String rideId;
    private String passengerId;
    private int driverRating;
    private int vehicleRating;
    private String comment;

    public RatingRequest(String rideId, String passengerId, int driverRating, int vehicleRating, String comment) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
    }

    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public int getDriverRating() { return driverRating; }
    public int getVehicleRating() { return vehicleRating; }
    public String getComment() { return comment; }
}
