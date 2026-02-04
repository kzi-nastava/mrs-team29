package domain.entities;

import domain.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "vehicles")
public class Vehicle {
	
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@NotBlank(message = "Vehicle model cannot be blank")
	@Column(name = "vehicle_model")
	private String vehicleModel;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VehicleType type;
	
	@NotBlank(message = "Registration plate cannot be blank")
	@Column(name = "registration_plate", unique = true, nullable = false)
	private String registrationPlate;
	
	@Positive(message = "Seats must be greater than 0")
	@Column(nullable = false)
	private int seats;
	
	@Column(name = "pets_allowed", nullable = false)
	private boolean petsAllowed;
	
	@Column(name = "babies_allowed", nullable = false)
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
	
	public String getId() {return id;}
	public String getVehicleModel() {return vehicleModel;}
	public VehicleType getType() {return type;}
	public String getRegistrationPlate() {return registrationPlate;}
	public int getSeats() {return seats;}
	public boolean isPetsAllowed() {return petsAllowed;}
	public boolean isBabiesAllowed() {return babiesAllowed;}
	
	public void setId(String id) {this.id = id;}
	public void setVehicleModel(String vehicleModel) {this.vehicleModel = vehicleModel;}
	public void setType(VehicleType type) {this.type = type;}
	public void setRegistrationPlate(String registrationPlate) {this.registrationPlate = registrationPlate;}
	public void setSeats(int seats) {this.seats = seats;}
	public void setPetsAllowed(boolean petsAllowed) {this.petsAllowed = petsAllowed;}
	public void setBabiesAllowed(boolean babiesAllowed) {this.babiesAllowed = babiesAllowed;}
}
