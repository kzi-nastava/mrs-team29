package dto.user;

import domain.entities.*;
import domain.enums.*;

public class UserProfileDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String username;
    private String email;
    private String phoneNumber;
    private Address address;
    private String profilePictureUrl;
    private UserType userType;

    public UserProfileDTO() {}

    // === GETTERS ===
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Address getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public UserType getUserType() { return userType; }

    // === SETTERS ===
    public void setId(Long id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(Address address) { this.address = address; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setUserType(UserType userType) { this.userType = userType; }
}
