package dto.ride;

public class RideFinishResponseDTO {

    private String driverStatus;
    private String nextRideId;

    public RideFinishResponseDTO() {}

    public RideFinishResponseDTO(String driverStatus, String nextRideId) {
        this.driverStatus = driverStatus;
        this.nextRideId = nextRideId;
    }

    public String getDriverStatus() { return driverStatus; }
    public String getNextRideId() { return nextRideId; }

    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }
    public void setNextRideId(String nextRideId) { this.nextRideId = nextRideId; }
}

