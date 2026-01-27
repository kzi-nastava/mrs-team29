package dto.ride;

import domain.entities.Address;
import java.util.List;

public class RideOrderDTO {

	private String creatorId;
    private Address pickupAddress;
    private Address destinationAddress;
    private List<Address> stops;

    private List<String> passengerIds; // client IDs
    private boolean pets;
    private boolean baby;

    public RideOrderDTO() {}

    public String getCreatorId() { return creatorId; }
    public Address getPickupAddress() { return pickupAddress; }
    public Address getDestinationAddress() { return destinationAddress; }
    public List<Address> getStops() { return stops; }
    public List<String> getPassengerIds() { return passengerIds; }
    public boolean isPets() { return pets; }
    public boolean isBaby() { return baby; }

    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setPickupAddress(Address pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDestinationAddress(Address destinationAddress) { this.destinationAddress = destinationAddress; }
    public void setStops(List<Address> stops) { this.stops = stops; }
    public void setPassengerIds(List<String> passengerIds) { this.passengerIds = passengerIds; }
    public void setPets(boolean pets) { this.pets = pets; }
    public void setBaby(boolean baby) { this.baby = baby; }
}
