package dto.auth;

public class LoginResponseDTO {
    
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role; // USER, DRIVER, ADMIN
    private String token; // JWT token
    private boolean isDriver;
    private String driverId;
    
    public LoginResponseDTO() {}
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public boolean isDriver() { return isDriver; }
    public void setDriver(boolean driver) { isDriver = driver; }
    
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
}
