package dto.ride;

public class RideFinishDTO {

    private String rideId;
    private String driverId;
    private boolean paid;

    public RideFinishDTO() {}

    public RideFinishDTO(String rideId, String driverId, boolean paid) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.paid = paid;
    }

    public String getRideId() { return rideId; }
    public String getDriverId() { return driverId; }
    public boolean isPaid() { return paid; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
