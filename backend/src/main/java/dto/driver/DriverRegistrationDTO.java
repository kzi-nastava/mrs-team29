package dto.driver;

import domain.entities.*;
import domain.enums.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class DriverRegistrationDTO {

    // === USER DATA ===
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private Gender gender;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    private UserType userType;
    private String phoneNumber;
    private Address address;
    private String profilePictureUrl;

    // === VEHICLE DATA ===
    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;
    
    private VehicleType vehicleType;
    
    @NotBlank(message = "Registration plate is required")
    private String registrationPlate;
    
    @Positive(message = "Seats must be greater than 0")
    private int seats;
    private boolean allowsPets;
    private boolean allowsBabies;

    public DriverRegistrationDTO() {}

    // ===== GETTERS =====
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserType getUserType() { return userType; }
    public String getPhoneNumber() { return phoneNumber; }
    public Address getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }

    public String getVehicleModel() { return vehicleModel; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
    public int getSeats() { return seats; }
    public boolean isAllowsPets() { return allowsPets; }
    public boolean isAllowsBabies() { return allowsBabies; }

    // ===== SETTERS =====
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(Address address) { this.address = address; }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }
    public void setSeats(int seats) { this.seats = seats; }
    public void setAllowsPets(boolean allowsPets) { this.allowsPets = allowsPets; }
    public void setAllowsBabies(boolean allowsBabies) { this.allowsBabies = allowsBabies; }
    
    public Vehicle toVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel(vehicleModel);
        vehicle.setType(vehicleType);
        vehicle.setRegistrationPlate(registrationPlate);
        vehicle.setSeats(seats);
        vehicle.setPetsAllowed(allowsPets);
        vehicle.setBabiesAllowed(allowsBabies);
        return vehicle;
    }
    
}
