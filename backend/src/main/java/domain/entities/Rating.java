package domain.entities;

import java.time.LocalDateTime;

public class Rating {

    private String id;

    // Connections
    private String rideId;
    private String passengerId;
    private String driverId;
    private String vehicleId;

    // Ratings
    private int driverRating;    // 1–5
    private int vehicleRating;   // 1–5
    private String comment;

    // Time
    private LocalDateTime rideFinishedAt;
    private LocalDateTime ratedAt;

    // Status
    private boolean expired;

    public Rating() {}

    public Rating(String id, String rideId, String passengerId, String driverId,
                  String vehicleId, int driverRating, int vehicleRating, String comment,
                  LocalDateTime rideFinishedAt, LocalDateTime ratedAt, boolean expired) {

        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.rideFinishedAt = rideFinishedAt;
        this.ratedAt = ratedAt;
        this.expired = expired;
    }

    public String getId() { return id; }
    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public String getDriverId() { return driverId; }
    public String getVehicleId() { return vehicleId; }
    public int getDriverRating() { return driverRating; }
    public int getVehicleRating() { return vehicleRating; }
    public String getComment() { return comment; }
    public LocalDateTime getRideFinishedAt() { return rideFinishedAt; }
    public LocalDateTime getRatedAt() { return ratedAt; }
    public boolean isExpired() { return expired; }

    public void setId(String id) { this.id = id; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public void setDriverRating(int driverRating) { this.driverRating = driverRating; }
    public void setVehicleRating(int vehicleRating) { this.vehicleRating = vehicleRating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRideFinishedAt(LocalDateTime rideFinishedAt) {
        this.rideFinishedAt = rideFinishedAt;
    }
    public void setRatedAt(LocalDateTime ratedAt) { this.ratedAt = ratedAt; }
    public void setExpired(boolean expired) { this.expired = expired; }
}

