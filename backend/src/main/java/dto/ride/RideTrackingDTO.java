package dto.ride;

public class RideTrackingDTO {

    private String rideId;

    private double vehicleLatitude;
    private double vehicleLongitude;

    private int estimatedTimeInMinutes;

    public RideTrackingDTO() {}

    public RideTrackingDTO(String rideId,
                           double vehicleLatitude,
                           double vehicleLongitude,
                           int estimatedTimeInMinutes) {
        this.rideId = rideId;
        this.vehicleLatitude = vehicleLatitude;
        this.vehicleLongitude = vehicleLongitude;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }

    public String getRideId() { return rideId; }
    public double getVehicleLatitude() { return vehicleLatitude; }
    public double getVehicleLongitude() { return vehicleLongitude; }
    public int getEstimatedTimeInMinutes() { return estimatedTimeInMinutes; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setVehicleLatitude(double vehicleLatitude) { this.vehicleLatitude = vehicleLatitude; }
    public void setVehicleLongitude(double vehicleLongitude) { this.vehicleLongitude = vehicleLongitude; }
    public void setEstimatedTimeInMinutes(int estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }
}

