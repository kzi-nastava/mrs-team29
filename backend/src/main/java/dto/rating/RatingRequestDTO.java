package dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RatingRequestDTO {

    @NotBlank(message = "Ride ID is required")
    private String rideId;
    
    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @Min(value = 1, message = "Driver rating must be at least 1")
    @Max(value = 5, message = "Driver rating must not exceed 5")
    private int driverRating;   // 1–5
    
    @Min(value = 1, message = "Vehicle rating must be at least 1")
    @Max(value = 5, message = "Vehicle rating must not exceed 5")
    private int vehicleRating;  // 1–5
    private String comment;

    public RatingRequestDTO() {}

    public String getRideId() { return rideId; }
    public String getPassengerId() { return passengerId; }
    public int getDriverRating() { return driverRating; }
    public int getVehicleRating() { return vehicleRating; }
    public String getComment() { return comment; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setDriverRating(int driverRating) { this.driverRating = driverRating; }
    public void setVehicleRating(int vehicleRating) { this.vehicleRating = vehicleRating; }
    public void setComment(String comment) { this.comment = comment; }
}
