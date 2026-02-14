package com.example.driverr_mobile.data.model;

public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String username;
    private String phoneNumber;
    private Object address;
    private String profilePictureUrl;

    public UpdateUserProfileRequest() {}

    public UpdateUserProfileRequest(
            String firstName,
            String lastName,
            String gender,
            String username,
            String phoneNumber,
            Object address,
            String profilePictureUrl
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public Object getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
}
