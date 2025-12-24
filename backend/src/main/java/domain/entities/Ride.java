package domain.entities;

import java.time.LocalDate;
import java.util.List;

import domain.enums.RideStatus;

public class Ride {
	private String id;
	private Address pickupAddress;
	private Address destinationAddress;
	private List<Address> stops;
	private List<User> passengers;
	private Driver driver;
	private RideStatus status;
	private double price;
	private List<LocalDate> timestamps; //Either change with Map<Address, LocalTime> or create class RideStops
	
	public Ride() {}
	public Ride(String id, Address pickupAddress, Address destinationAddress, List<Address> stops, List<User> passengers,
				Driver driver, RideStatus status, double price, List<LocalDate> timestamps) {
		this.id = id;
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
	public List<LocalDate> getTimestamps() {return timestamps;} 
	
	public void setId(String id) {this.id = id;}
	public void setPickupAddress(Address pickupAddress) {this.pickupAddress = pickupAddress;}
	public void setDestinationAddress(Address destinationAddress) {this.destinationAddress = destinationAddress;}
	public void setStops(List<Address> stops) {this.stops = stops;}
	public void setPassengers(List<User> passengers) {this.passengers = passengers;}
	public void setDriver(Driver driver) {this.driver = driver;}
	public void setStatus(RideStatus status) {this.status = status;}
	public void setPrice(double price) {this.price = price;}
	public void setTimestamps(List<LocalDate> timestamps) {this.timestamps = timestamps;}
	
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
