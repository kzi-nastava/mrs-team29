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

    // getters & setters
}

