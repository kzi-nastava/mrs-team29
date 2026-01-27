package domain.entities;

import domain.enums.DriverStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "driver")
public class Driver extends User{
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;
	
	@Enumerated(EnumType.STRING)
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
