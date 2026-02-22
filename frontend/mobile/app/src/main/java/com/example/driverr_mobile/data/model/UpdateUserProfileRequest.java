package com.example.driverr_mobile.data.model;

public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String username;
    private String phoneNumber;
    private Object address;
    private String profilePictureUrl;
    private String vehicleModel;
    private String vehicleType;
    private String registrationPlate;
    private Integer vehicleSeats;
    private Boolean petsAllowed;
    private Boolean babiesAllowed;

    public UpdateUserProfileRequest() {}

    public UpdateUserProfileRequest(
            String firstName,
            String lastName,
            String gender,
            String username,
            String phoneNumber,
            Object address,
            String profilePictureUrl,
            String vehicleModel,
            String vehicleType,
            String registrationPlate,
            Integer vehicleSeats,
            Boolean petsAllowed,
            Boolean babiesAllowed
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profilePictureUrl = profilePictureUrl;
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.registrationPlate = registrationPlate;
        this.vehicleSeats = vehicleSeats;
        this.petsAllowed = petsAllowed;
        this.babiesAllowed = babiesAllowed;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public Object getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
    public Integer getVehicleSeats() { return vehicleSeats; }
    public Boolean getPetsAllowed() { return petsAllowed; }
    public Boolean getBabiesAllowed() { return babiesAllowed; }
}
