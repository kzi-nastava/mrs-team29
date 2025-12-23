package main.java.domain.entities;

import main.java.domain.enums.DriverStatus;

public class Driver {
	private Vehicle vehicle;
	private DriverStatus status;
	
	public Driver() { super(); }
	public Driver(Vehicle vehicle, DriverStatus status) {
		super();
		this.vehicle = vehicle;
		this.status = status;
	}
	
	public Vehicle getVehicle() {return vehicle;}
	public DriverStatus getStatus() {return status;}
	
	public void setVehicle(Vehicle vehicle) {this.vehicle = vehicle;}
	public void setStatus(DriverStatus status) {this.status = status;}
}
