package dto.ride;

import domain.enums.*;
import domain.entities.*;

public class RideResponseDTO {

    private String rideId;
    private RideStatus status;
    private double price;
    private String driverId;

    public RideResponseDTO() {}

    public String getDriverId() { return driverId; }
    public String getRideId() { return rideId; }
    public RideStatus getStatus() { return status; }
    public double getPrice() { return price; }

    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }
    
    public static RideResponseDTO fromRide(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.rideId = ride.getId();
        dto.status = ride.getStatus();
        dto.price = ride.getPrice();
        dto.driverId = ride.getDriver() != null
                ? ride.getDriver().getId()
                : null;
        return dto;
    }
}

