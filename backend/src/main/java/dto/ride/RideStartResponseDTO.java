package dto.ride;

import java.time.LocalDateTime;

import domain.enums.*;

public class RideStartResponseDTO {

    private String rideId;
    private RideStatus status;
    private LocalDateTime startTime;

    public RideStartResponseDTO() {}

    public RideStartResponseDTO(String rideId, RideStatus status, LocalDateTime startTime) {
        this.rideId = rideId;
        this.status = status;
        this.startTime = startTime;
    }

    public String getRideId() {
        return rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}

