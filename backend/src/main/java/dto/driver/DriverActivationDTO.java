package dto.driver;

import jakarta.validation.constraints.NotBlank;

public class DriverActivationDTO {
    
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    public DriverActivationDTO() {}
    
    public String getToken() { return token; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    
    public void setToken(String token) { this.token = token; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
