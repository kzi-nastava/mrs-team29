package dto.user;

import domain.entities.Address;
import domain.enums.Gender;
import domain.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;

public class UpdateUserProfileDTO {
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private Gender gender;
    private String username;
    private String phoneNumber;
    private Address address;
    private String profilePictureUrl;
    private String vehicleModel;
    private VehicleType vehicleType;
    private String registrationPlate;
    private Integer vehicleSeats;
    private Boolean petsAllowed;
    private Boolean babiesAllowed;

    public UpdateUserProfileDTO() {}

    // === GETTERS ===
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public Address getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getVehicleModel() { return vehicleModel; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
    public Integer getVehicleSeats() { return vehicleSeats; }
    public Boolean getPetsAllowed() { return petsAllowed; }
    public Boolean getBabiesAllowed() { return babiesAllowed; }

    // === SETTERS ===
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setUsername(String username) { this.username = username; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(Address address) { this.address = address; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public void setRegistrationPlate(String registrationPlate) { this.registrationPlate = registrationPlate; }
    public void setVehicleSeats(Integer vehicleSeats) { this.vehicleSeats = vehicleSeats; }
    public void setPetsAllowed(Boolean petsAllowed) { this.petsAllowed = petsAllowed; }
    public void setBabiesAllowed(Boolean babiesAllowed) { this.babiesAllowed = babiesAllowed; }
}
