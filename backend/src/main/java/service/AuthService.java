package service;

import dto.auth.*;

public interface AuthService {
    
    // 2.2.1 - Login
    LoginResponseDTO login(LoginRequestDTO dto);
    
    // 2.2.1 - Logout
    void logout(String userId);
    
    // 2.2.1 - Driver status management
    void setDriverStatus(String driverId, boolean active);
    boolean canDriverLogout(String driverId);
    
    // 2.2.2 - User registration
    void registerUser(RegisterRequestDTO dto);
    void activateAccount(String token);
    
    // Password reset flow
    void requestPasswordReset(String email);
    void resetPassword(PasswordResetDTO dto);
}
