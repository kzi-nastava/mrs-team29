package dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    public PasswordResetRequestDTO() {}
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
