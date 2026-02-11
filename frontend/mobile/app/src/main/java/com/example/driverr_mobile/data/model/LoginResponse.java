package com.example.driverr_mobile.data.model;

public class LoginResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String token;
    private boolean isDriver;
    private String driverId;

    public LoginResponse() {}

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public boolean isDriver() { return isDriver; }
    public String getDriverId() { return driverId; }
}
