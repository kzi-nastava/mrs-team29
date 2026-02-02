package dto.driver;

import java.time.LocalDateTime;
import java.util.List;

public class DriverRideHistoryDTO {

    private String rideId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String startLocation;
    private String endLocation;

    private boolean canceled;
    private String canceledBy; // DRIVER / CLIENT / null

    private double price;
    private boolean panicActivated;

    private List<PassengerInfoDTO> passengers;

    public DriverRideHistoryDTO() {}

    public DriverRideHistoryDTO(String rideId,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                String startLocation,
                                String endLocation,
                                boolean canceled,
                                String canceledBy,
                                double price,
                                boolean panicActivated,
                                List<PassengerInfoDTO> passengers) {
        this.rideId = rideId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.canceled = canceled;
        this.canceledBy = canceledBy;
        this.price = price;
        this.panicActivated = panicActivated;
        this.passengers = passengers;
    }

    public String getRideId() { return rideId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public boolean isCanceled() { return canceled; }
    public String getCanceledBy() { return canceledBy; }
    public double getPrice() { return price; }
    public boolean isPanicActivated() { return panicActivated; }
    public List<PassengerInfoDTO> getPassengers() { return passengers; }

    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }
    public void setCanceled(boolean canceled) { this.canceled = canceled; }
    public void setCanceledBy(String canceledBy) { this.canceledBy = canceledBy; }
    public void setPrice(double price) { this.price = price; }
    public void setPanicActivated(boolean panicActivated) { this.panicActivated = panicActivated; }
    public void setPassengers(List<PassengerInfoDTO> passengers) { this.passengers = passengers; }
}

