package dto.ride;

import domain.enums.RideStatus;

public class RideResponseDTO {

    private String rideId;
    private RideStatus status;
    private double price;

    public RideResponseDTO() {}

    public String getRideId() { return rideId; }
    public RideStatus getStatus() { return status; }
    public double getPrice() { return price; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }
}

