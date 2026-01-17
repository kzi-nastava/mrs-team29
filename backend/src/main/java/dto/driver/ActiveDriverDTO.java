package dto.driver;

public class ActiveDriverDTO {

    private String driverId;
    private String vehicleId;

    private double latitude;
    private double longitude;

    private boolean occupied;

    public ActiveDriverDTO() {}

    public ActiveDriverDTO(String driverId, String vehicleId,
                           double latitude, double longitude,
                           boolean occupied) {
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.occupied = occupied;
    }

    public String getDriverId() { return driverId; }
    public String getVehicleId() { return vehicleId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isOccupied() { return occupied; }

    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}

