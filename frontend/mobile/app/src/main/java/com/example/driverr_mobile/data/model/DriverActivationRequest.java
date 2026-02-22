package com.example.driverr_mobile.data.model;

public class DriverActivationRequest {
    private String token;
    private String password;
    private String confirmPassword;

    public DriverActivationRequest() {}

    public DriverActivationRequest(String token, String password, String confirmPassword) {
        this.token = token;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getToken() { return token; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
}
