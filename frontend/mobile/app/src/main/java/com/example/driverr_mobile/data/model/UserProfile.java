package com.example.driverr_mobile.data.model;

public class UserProfile {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String username;
    private String email;
    private String phoneNumber;
    private Object address;
    private String profilePictureUrl;
    private String userType;
    private boolean blocked;
    private String blockNote;
    private String vehicleModel;
    private String vehicleType;
    private String registrationPlate;
    private Integer vehicleSeats;
    private Boolean petsAllowed;
    private Boolean babiesAllowed;

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Object getAddress() { return address; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getUserType() { return userType; }
    public boolean isBlocked() { return blocked; }
    public String getBlockNote() { return blockNote; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
    public Integer getVehicleSeats() { return vehicleSeats; }
    public Boolean getPetsAllowed() { return petsAllowed; }
    public Boolean getBabiesAllowed() { return babiesAllowed; }
}
