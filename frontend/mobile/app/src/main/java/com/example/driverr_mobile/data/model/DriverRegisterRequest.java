package com.example.driverr_mobile.data.model;

public class DriverRegisterRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String vehicleModel;
    private String vehicleType;
    private String registrationPlate;
    private int seats;
    private boolean allowsPets;
    private boolean allowsBabies;

    public DriverRegisterRequest() {}

    public DriverRegisterRequest(
            String firstName,
            String lastName,
            String gender,
            String username,
            String email,
            String password,
            String phoneNumber,
            String vehicleModel,
            String vehicleType,
            String registrationPlate,
            int seats,
            boolean allowsPets,
            boolean allowsBabies
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.registrationPlate = registrationPlate;
        this.seats = seats;
        this.allowsPets = allowsPets;
        this.allowsBabies = allowsBabies;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleType() { return vehicleType; }
    public String getRegistrationPlate() { return registrationPlate; }
    public int getSeats() { return seats; }
    public boolean isAllowsPets() { return allowsPets; }
    public boolean isAllowsBabies() { return allowsBabies; }
}
