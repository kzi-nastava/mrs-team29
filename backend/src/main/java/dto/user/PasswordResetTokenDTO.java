package dto.user;

import jakarta.validation.constraints.NotBlank;

public class PasswordResetTokenDTO {
    
    @NotBlank(message = "Token cannot be blank")
    private String token;
    
    @NotBlank(message = "New password cannot be blank")
    private String newPassword;
    
    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;
    
    public PasswordResetTokenDTO() {}
    
    public String getToken() { return token; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    
    public void setToken(String token) { this.token = token; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
