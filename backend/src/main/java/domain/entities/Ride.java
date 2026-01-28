package domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import domain.enums.RideStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "ride")
public class Ride {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@ManyToOne
    @JoinColumn(name = "pickup_address_id")
	private Address pickupAddress;
	
	@ManyToOne
    @JoinColumn(name = "destination_address_id")
	private Address destinationAddress;
	
	@ManyToMany
    @JoinTable(
        name = "ride_stops",
        joinColumns = @JoinColumn(name = "ride_id"),
        inverseJoinColumns = @JoinColumn(name = "address_id")
    )
	private List<Address> stops;
	
	@ManyToMany
    @JoinTable(
        name = "ride_passengers",
        joinColumns = @JoinColumn(name = "ride_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
	private List<User> passengers;
	
	@ManyToOne
    @JoinColumn(name = "driver_id")
	private Driver driver;
	
	@Enumerated(EnumType.STRING)
	private RideStatus status;
	
	private double price;
	
	@ElementCollection
    @CollectionTable(name = "ride_timestamps", joinColumns = @JoinColumn(name = "ride_id"))
	private List<LocalDateTime> timestamps;
	
	public Ride() {}
	public Ride(Address pickupAddress, Address destinationAddress, List<Address> stops, List<User> passengers,
				Driver driver, RideStatus status, double price, List<LocalDateTime> timestamps) {
		this.pickupAddress = pickupAddress;
		this.destinationAddress = destinationAddress;
		this.stops = stops;
		this.passengers = passengers;
		this.driver = driver;
		this.status = status;
		this.price = price;
		this.timestamps = timestamps;
	}
	
	public String getId() {return id;}
	public Address getPickupAddress() {return pickupAddress;}
	public Address getDestinationAddress() {return destinationAddress;}
	public List<Address> getStops() {return stops;}
	public List<User> getPassengers() {return passengers;}
	public Driver getDriver() {return driver;}
	public RideStatus getStatus() {return status;}
	public double getPrice() {return price;}
	public List<LocalDateTime> getTimestamps() {return timestamps;} 
	
	public void setId(String id) {this.id = id;}
	public void setPickupAddress(Address pickupAddress) {this.pickupAddress = pickupAddress;}
	public void setDestinationAddress(Address destinationAddress) {this.destinationAddress = destinationAddress;}
	public void setStops(List<Address> stops) {this.stops = stops;}
	public void setPassengers(List<User> passengers) {this.passengers = passengers;}
	public void setDriver(Driver driver) {this.driver = driver;}
	public void setStatus(RideStatus status) {this.status = status;}
	public void setPrice(double price) {this.price = price;}
	public void setTimestamps(List<LocalDateTime> list) {this.timestamps = list;}
	
	//These classes will be moved into RideService when made
	
	/*public void addPassenger(User passenger) {
		List<User> passengers = ride.getPassengers();
		passengers.add(passenger);
		setPassenger(passengers);
	}
	
	public void addTimestamp(LocalDate timestamp) {
		List<LocalDate> timestamps = ride.getTimestamps();
		timestamps.add(timestamp);
		setTimestamps(timestamps);
	}
	
	public void addStops(Address stop) {
		List<Address> stops = ride.getStops();
		stops.add(stop);
		setStops(stops);
	}*/
}
