package dto.ride;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class RideOrderDTO {

    @NotBlank(message = "Creator ID is required")
    private String creatorId;

    @NotBlank(message = "Pickup address ID is required")
    private String pickupAddressId;
    
    @NotBlank(message = "Destination address ID is required")
    private String destinationAddressId;
    private List<String> stopAddressIds;

    private List<String> passengerIds;

    private boolean pets;
    private boolean baby;

    public RideOrderDTO() {}

    public String getCreatorId() { return creatorId; }
    public String getPickupAddressId() { return pickupAddressId; }
    public String getDestinationAddressId() { return destinationAddressId; }
    public List<String> getStopAddressIds() { return stopAddressIds; }
    public List<String> getPassengerIds() { return passengerIds; }
    public boolean isPets() { return pets; }
    public boolean isBaby() { return baby; }

    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setPickupAddressId(String pickupAddressId) { this.pickupAddressId = pickupAddressId; }
    public void setDestinationAddressId(String destinationAddressId) { this.destinationAddressId = destinationAddressId; }
    public void setStopAddressIds(List<String> stopAddressIds) { this.stopAddressIds = stopAddressIds; }
    public void setPassengerIds(List<String> passengerIds) { this.passengerIds = passengerIds; }
    public void setPets(boolean pets) { this.pets = pets; }
    public void setBaby(boolean baby) { this.baby = baby; }
}
