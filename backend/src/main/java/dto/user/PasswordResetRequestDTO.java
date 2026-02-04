package dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequestDTO {
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    
    public PasswordResetRequestDTO() {}
    
    public PasswordResetRequestDTO(String email) {
        this.email = email;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
