package dto.ride;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import domain.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RideOrderDTO {

    @NotBlank(message = "Creator ID is required")
    private String creatorId;

    @NotBlank(message = "Pickup address ID is required")
    private String pickupAddressId;
    
    @NotBlank(message = "Destination address ID is required")
    private String destinationAddressId;
    
    private List<String> stopAddressIds;
    private List<String> passengerIds;
    
    private VehicleType vehicleType;
    private boolean pets;
    private boolean baby;
    private String notes;
    
    private LocalDateTime scheduledTime;  // null = immediate, otherwise future ride

    public RideOrderDTO() {}

    public String getCreatorId() { return creatorId; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public List<String> getStopAddressIds() { return stopAddressIds; }
    public List<String> getPassengerIds() { return passengerIds; }
    public VehicleType getVehicleType() { return vehicleType; }
    public boolean isPets() { return pets; }
    public boolean isBaby() { return baby; }
    public String getNotes() { return notes; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }

    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setPickupAddressId(String pickupAddressId) { this.pickupAddressId = pickupAddressId; }
    public void setDestinationAddressId(String destinationAddressId) { this.destinationAddressId = destinationAddressId; }
    public void setStopAddressIds(List<String> stopAddressIds) { this.stopAddressIds = stopAddressIds; }
    public void setPassengerIds(List<String> passengerIds) { this.passengerIds = passengerIds; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public void setPets(boolean pets) { this.pets = pets; }
    public void setBaby(boolean baby) { this.baby = baby; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
}
