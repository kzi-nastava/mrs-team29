package dto.ride;

import domain.entities.Ride;
import domain.entities.Address;
import domain.entities.User;
import domain.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RideResponseDTO {

    private String rideId;
    private RideStatus status;
    private double price;
    private String driverId;
    private String driverName;
    private String pickupAddressId;
    private String pickupAddress;
    private String destinationAddressId;
    private String destinationAddress;
    private List<String> stopAddressIds;
    private List<String> passengerIds;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime scheduledTime;

    public RideResponseDTO() {}

    // Getters
    public String getRideId() { return rideId; }
    public RideStatus getStatus() { return status; }
    public double getPrice() { return price; }
    public String getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public String getDestinationAddress() { return destinationAddress; }
    public List<String> getStopAddressIds() { return stopAddressIds; }
    public List<String> getPassengerIds() { return passengerIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }

    // Setters
    public void setRideId(String rideId) { this.rideId = rideId; }
    public void setStatus(RideStatus status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setPickupAddressId(String pickupAddressId) { this.pickupAddressId = pickupAddressId; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDestinationAddressId(String destinationAddressId) { this.destinationAddressId = destinationAddressId; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    public void setStopAddressIds(List<String> stopAddressIds) { this.stopAddressIds = stopAddressIds; }
    public void setPassengerIds(List<String> passengerIds) { this.passengerIds = passengerIds; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public static RideResponseDTO fromRide(Ride ride) {
        RideResponseDTO dto = new RideResponseDTO();
        dto.rideId = ride.getId();
        dto.status = ride.getStatus();
        dto.price = ride.getPrice();
        dto.scheduledTime = ride.getScheduledTime();
        
        // Driver info
        if (ride.getDriver() != null) {
            dto.driverId = ride.getDriver().getId();
            dto.driverName = ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName();
        }
        
        // Address info
        if (ride.getPickupAddress() != null) {
            dto.pickupAddressId = ride.getPickupAddress().getId();
            dto.pickupAddress = formatAddress(ride.getPickupAddress());
        }
        if (ride.getDestinationAddress() != null) {
            dto.destinationAddressId = ride.getDestinationAddress().getId();
            dto.destinationAddress = formatAddress(ride.getDestinationAddress());
        }
        
        // Stop addresses
        if (ride.getStops() != null && !ride.getStops().isEmpty()) {
            dto.stopAddressIds = ride.getStops().stream()
                .map(Address::getId)
                .collect(Collectors.toList());
        }
        
        // Passenger IDs
        if (ride.getPassengers() != null && !ride.getPassengers().isEmpty()) {
            dto.passengerIds = ride.getPassengers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        }
        
        // Timestamps
        if (ride.getTimestamps() != null && !ride.getTimestamps().isEmpty()) {
            dto.createdAt = ride.getTimestamps().get(0);
            if (ride.getTimestamps().size() > 1) {
                dto.startedAt = ride.getTimestamps().get(1);
            }
            if (ride.getTimestamps().size() > 2) {
                dto.finishedAt = ride.getTimestamps().get(2);
            }
        }
        
        return dto;
    }
    
    private static String formatAddress(Address address) {
        return address.getStreet() + " " + address.getStreetNumber() + ", " + address.getCity();
    }
}
