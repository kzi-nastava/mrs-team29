package service;

import java.util.List;

import domain.entities.User;
import dto.user.*;

public interface UserService {
	public User register(User user);
	public User getByUsername(String username);
	public List<User> getAll();
	UserProfileDTO getUserProfile(String userId);
    UserProfileDTO updateProfile(String userId, UpdateUserProfileDTO dto);
    void changePassword(String userId, ChangePasswordDTO dto);
    void changePassword(String userId, PasswordResetDTO dto);
    List<ProfileChangeRequestDTO> getProfileChangeRequests(String userId);
    List<ProfileChangeRequestDTO> getAllPendingProfileChangeRequests();
    void approveProfileChangeRequest(String requestId);
    void rejectProfileChangeRequest(String requestId);
    
    // Authentication methods
    LoginResponseDTO login(LoginRequestDTO dto);
    void logout(String userId);
    void registerUser(RegisterRequestDTO dto);
    void activateAccount(String token);
    void requestPasswordReset(String email);
    void resetPasswordWithToken(PasswordResetTokenDTO dto);
    void setDriverStatus(String driverId, boolean active);
    boolean canDriverLogout(String driverId);
}
