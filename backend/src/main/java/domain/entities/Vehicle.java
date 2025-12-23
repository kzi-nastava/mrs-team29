package main.java.domain.entities;

import main.java.domain.enums.VehicleType;

public class Vehicle {
	private String vehicleModel;
	private VehicleType type;
	private String registrationPlate;
	private int seats;
	private boolean petsAllowed;
	private boolean babiesAllowed;
	
	public Vehicle() {}
	
	public Vehicle(String vehicleModel, VehicleType type, String registrationPlate, int seats,
					boolean petsAllowed, boolean babiesAllowed) {
		this.vehicleModel = vehicleModel;
		this.type = type;
		this.registrationPlate = registrationPlate;
		this.seats = seats;
		this.petsAllowed = petsAllowed;
		this.babiesAllowed = babiesAllowed;
	}
	
	public String getVehicleModel() {return vehicleModel;}
	public VehicleType getType() {return type;}
	public String getRegistrationPlate() {return registrationPlate;}
	public int getSeats() {return seats;}
	public boolean isPetsAllowed() {return petsAllowed;}
	public boolean isBabiesAllowed() {return babiesAllowed;}
	
	public void setVehicleModel(String vehicleModel) {this.vehicleModel = vehicleModel;}
	public void setType(VehicleType type) {this.type = type;}
	public void setRegistrationPlate(String registrationPlate) {this.registrationPlate = registrationPlate;}
	public void setSeats(int seats) {this.seats = seats;}
	public void setPetsAllowed(boolean petsAllowed) {this.petsAllowed = petsAllowed;}
	public void setBabiesAllowed(boolean babiesAllowed) {this.babiesAllowed = babiesAllowed;}
}
