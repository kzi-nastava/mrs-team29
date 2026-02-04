package dto.user;

public class LoginResponseDTO {
    
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String token;
    private boolean isDriver;
    private String driverId;
    
    public LoginResponseDTO() {}
    
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public boolean isDriver() { return isDriver; }
    public String getDriverId() { return driverId; }
    
    public void setUserId(String userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(String role) { this.role = role; }
    public void setToken(String token) { this.token = token; }
    public void setDriver(boolean isDriver) { this.isDriver = isDriver; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
}
