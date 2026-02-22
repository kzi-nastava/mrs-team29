package dto.user;

import domain.entities.Address;
import domain.entities.Driver;
import domain.entities.User;
import domain.entities.Vehicle;
import domain.enums.Gender;
import domain.enums.UserType;
import domain.enums.VehicleType;

public class UserProfileDTO {

    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String username;
    private String email;
    private String phoneNumber;
    private Address address;
    private String profilePictureUrl;
    private UserType userType;
    private boolean isBlocked;
    private String blockNote;
    private String vehicleModel;
    private VehicleType vehicleType;
    private String registrationPlate;
    private Integer vehicleSeats;
    private Boolean petsAllowed;
    private Boolean babiesAllowed;

    public UserProfileDTO() {}

    // GETTERS
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Address getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public UserType getUserType() { return userType; }
    public boolean isBlocked() { return isBlocked; }
    public String getBlockNote() { return blockNote; }
    public String getVehicleModel() { return vehicleModel; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
    public Integer getVehicleSeats() { return vehicleSeats; }
    public Boolean getPetsAllowed() { return petsAllowed; }
    public Boolean getBabiesAllowed() { return babiesAllowed; }

    // SETTERS
    public void setId(String id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(Address address) { this.address = address; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }
    public void setBlockNote(String blockNote) { this.blockNote = blockNote; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public void setRegistrationPlate(String registrationPlate) { this.registrationPlate = registrationPlate; }
    public void setVehicleSeats(Integer vehicleSeats) { this.vehicleSeats = vehicleSeats; }
    public void setPetsAllowed(Boolean petsAllowed) { this.petsAllowed = petsAllowed; }
    public void setBabiesAllowed(Boolean babiesAllowed) { this.babiesAllowed = babiesAllowed; }

    public static UserProfileDTO fromUser(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.id = user.getId();
        dto.firstName = user.getFirstName();
        dto.lastName = user.getLastName();
        dto.gender = user.getGender();
        dto.username = user.getUserName();
        dto.email = user.getEmail();
        dto.phoneNumber = user.getPhoneNumber();
        dto.address = user.getAddress();
        dto.isBlocked = user.getIsBlocked();
        dto.blockNote = user.getBlockNote();
        dto.profilePictureUrl = user.getProfilePictureUrl();
        dto.userType = user.getUserType();

        if (user instanceof Driver driver && driver.getVehicle() != null) {
            Vehicle vehicle = driver.getVehicle();
            dto.vehicleModel = vehicle.getVehicleModel();
            dto.vehicleType = vehicle.getType();
            dto.registrationPlate = vehicle.getRegistrationPlate();
            dto.vehicleSeats = vehicle.getSeats();
            dto.petsAllowed = vehicle.isPetsAllowed();
            dto.babiesAllowed = vehicle.isBabiesAllowed();
        }

        return dto;
    }
}
