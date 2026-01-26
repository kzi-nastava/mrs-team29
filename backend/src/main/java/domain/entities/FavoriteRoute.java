package domain.entities;

import java.util.List;

public class FavoriteRoute {
    private String id;
    private User user;
    private Address pickupAddress;
    private Address destinationAddress;
    private List<Address> stops;
    private String name;

	public FavoriteRoute() {}
	
	public FavoriteRoute(String id, User user, Address pickupAddress, Address destinationAddress,
			List<Address> stops, String name) {
		this.id = id;
		this.user = user;
		this.pickupAddress = pickupAddress;
		this.destinationAddress = destinationAddress;
		this.stops = stops;
		this.name = name;
	}
	
	public String getId() { return id; }
	public User getUser() { return user; }
	public Address getPickupAddress() { return pickupAddress; }
	public Address getDestinationAddress() { return destinationAddress; }
	public List<Address> getStops() { return stops; }
	public String getName() { return name; }
	
	public void setId(String id) { this.id = id; }
	public void setUser(User user) { this.user = user; }
	public void setPickupAddress(Address pickupAddress) { this.pickupAddress = pickupAddress; }
	public void setDestinationAddress(Address destinationAddress) { this.destinationAddress = destinationAddress; }
	public void setStops(List<Address> stops) { this.stops = stops; }
	public void addStop(Address stop) { stops.add(stop); }
	public void setName(String name) { this.name = name; }
}	