package dto.user;

import domain.entities.Address;
import domain.enums.Gender;
import domain.enums.UserType;

public class UpdateUserProfileDTO {
    private String firstName;
    private String lastName;
    private Gender gender;
    private String username;
    private String phoneNumber;
    private Address address;
    private String profilePictureUrl;

    public UpdateUserProfileDTO() {}

    // === GETTERS ===
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public Address getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }

    // === SETTERS ===
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setUsername(String username) { this.username = username; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(Address address) { this.address = address; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
