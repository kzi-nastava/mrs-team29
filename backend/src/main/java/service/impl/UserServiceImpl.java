package service.impl;

import dto.user.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import domain.entities.*;
import domain.enums.*;
import service.UserService;
import service.EmailService;
import repository.*;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
    private final ProfileChangeRequestRepository profileChangeRequestRepository;
    private final DriverRepository driverRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final RideRepository rideRepository;
    private final EmailService emailService;
    
    private static final String DEFAULT_PROFILE_PICTURE = "https://via.placeholder.com/150";

    public UserServiceImpl(UserRepository userRepository,
                           ProfileChangeRequestRepository profileChangeRequestRepository,
                           DriverRepository driverRepository,
                           ActivationTokenRepository activationTokenRepository,
                           RideRepository rideRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.profileChangeRequestRepository = profileChangeRequestRepository;
        this.driverRepository = driverRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.rideRepository = rideRepository;
        this.emailService = emailService;
    }
    
    // ==================== Authentication Methods ====================
    
    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Find user by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Check if account is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account not activated. Please check your email.");
        }
        
        // Verify password (in production, use BCrypt)
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Check if user is a driver and set driver to active
        Driver driver = null;
        if (user instanceof Driver) {
            driver = (Driver) user;
            driver.setStatus(DriverStatus.ACTIVE);
            driverRepository.save(driver);
        } else {
            driver = driverRepository.findById(user.getId()).orElse(null);
            if (driver != null) {
                driver.setStatus(DriverStatus.ACTIVE);
                driverRepository.save(driver);
            }
        }
        
        // Build response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getUserType() != null ? user.getUserType().name() : "USER");
        response.setToken("mock-jwt-token-" + UUID.randomUUID());
        
        if (driver != null) {
            response.setDriver(true);
            response.setDriverId(driver.getId());
        }
        
        return response;
    }
    
    @Override
    @Transactional
    public void logout(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is a driver
        Driver driver = null;
        if (user instanceof Driver) {
            driver = (Driver) user;
        } else {
            driver = driverRepository.findById(userId).orElse(null);
        }
        
        if (driver != null) {
            // Check if driver can logout (no active ride)
            if (!canDriverLogout(driver.getId())) {
                throw new RuntimeException("Cannot logout while having an active ride");
            }
            
            // Set driver to inactive
            driver.setStatus(DriverStatus.INACTIVE);
            driverRepository.save(driver);
        }
    }
    
    @Override
    @Transactional
    public void registerUser(RegisterRequestDTO dto) {
        // Validate passwords match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // In production, hash with BCrypt
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserType(UserType.CLIENT); // Default to client
        user.setUserName(dto.getEmail()); // Use email as username
        user.setIsActive(false); // Not activated until email confirmation
        user.setIsBlocked(false);
        
        // Set default profile picture if not provided
        if (dto.getProfilePictureUrl() == null || dto.getProfilePictureUrl().isEmpty()) {
            user.setProfilePictureUrl(DEFAULT_PROFILE_PICTURE);
        } else {
            user.setProfilePictureUrl(dto.getProfilePictureUrl());
        }
        
        userRepository.save(user);
        
        // Create activation token (valid for 24 hours)
        ActivationToken token = new ActivationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsed(false);
        
        activationTokenRepository.save(token);
        
        // Send activation email
        String fullName = user.getFirstName() + " " + user.getLastName();
        emailService.sendActivationEmail(user.getEmail(), fullName, token.getToken());
        
        System.out.println("Activation email sent to " + user.getEmail());
    }
    
    @Override
    @Transactional
    public void activateAccount(String tokenString) {
        ActivationToken token = activationTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));
        
        // Check if token is expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Activation token has expired");
        }
        
        // Check if token was already used
        if (token.isUsed()) {
            throw new RuntimeException("Activation token already used");
        }
        
        // Activate user
        User user = token.getUser();
        user.setIsActive(true);
        userRepository.save(user);
        
        // Mark token as used
        token.setUsed(true);
        activationTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void activateAccountByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsActive()) {
            return;
        }

        user.setIsActive(true);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create password reset token (valid for 1 hour)
        ActivationToken token = new ActivationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setUsed(false);
        
        activationTokenRepository.save(token);
        
        // Send password reset email
        String fullName = user.getFirstName() + " " + user.getLastName();
        emailService.sendPasswordResetEmail(user.getEmail(), fullName, token.getToken());
        
        System.out.println("Password reset email sent to " + user.getEmail());
    }
    
    @Override
    @Transactional
    public void resetPasswordWithToken(PasswordResetTokenDTO dto) {
        // Validate passwords match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        ActivationToken token = activationTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        // Check if token is expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        // Check if token was already used
        if (token.isUsed()) {
            throw new RuntimeException("Reset token already used");
        }
        
        // Update password
        User user = token.getUser();
        user.setPassword(dto.getNewPassword()); // In production, hash with BCrypt
        userRepository.save(user);
        
        // Mark token as used
        token.setUsed(true);
        activationTokenRepository.save(token);
    }
    
    @Override
    @Transactional
    public void setDriverStatus(String driverId, boolean active) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        DriverStatus newStatus = active ? DriverStatus.ACTIVE : DriverStatus.INACTIVE;
        driver.setStatus(newStatus);
        driverRepository.save(driver);
    }
    
    @Override
    public boolean canDriverLogout(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        // Check if driver has any active rides
        long activeRides = rideRepository.findByDriver_IdAndStatus(driverId, RideStatus.ACTIVE).size();
        return activeRides == 0;
    }
    
    // ==================== User Profile Methods ====================
	
    @Override
    public UserProfileDTO getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileDTO.fromUser(user);
    }

    @Override
    public UserProfileDTO updateProfile(String userId, UpdateUserProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserType() == UserType.DRIVER) {
            ProfileChangeRequest request = new ProfileChangeRequest(
                    user,
                    "PROFILE_UPDATE",
                    user.toString(),
                    dto.toString()
            );

            profileChangeRequestRepository.save(request);
            return UserProfileDTO.fromUser(user);
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setProfilePictureUrl(dto.getProfilePictureUrl());

        userRepository.save(user);
        return UserProfileDTO.fromUser(user);
    }
    
    @Override
    public void changePassword(String userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }
    
    @Override
    public void changePassword(String userId, PasswordResetDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }
    
    @Override
    public List<ProfileChangeRequestDTO> getProfileChangeRequests(String userId) {
        List<ProfileChangeRequest> requests = profileChangeRequestRepository.findByUser_Id(userId);
        return requests.stream()
            .map(this::mapToDTO)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<ProfileChangeRequestDTO> getAllPendingProfileChangeRequests() {
        List<ProfileChangeRequest> requests = profileChangeRequestRepository
            .findByStatus(ChangeRequestStatus.PENDING);
        return requests.stream()
            .map(this::mapToDTO)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public void approveProfileChangeRequest(String requestId) {
        ProfileChangeRequest request = profileChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(ChangeRequestStatus.APPROVED);
        profileChangeRequestRepository.save(request);
        
        User user = request.getUser();
        applyProfileChange(user, request.getFieldName(), request.getNewValue());
        userRepository.save(user);
    }
    
    @Override
    public void rejectProfileChangeRequest(String requestId) {
        ProfileChangeRequest request = profileChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(ChangeRequestStatus.REJECTED);
        profileChangeRequestRepository.save(request);
    }
    
    private ProfileChangeRequestDTO mapToDTO(ProfileChangeRequest request) {
        ProfileChangeRequestDTO dto = new ProfileChangeRequestDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUser().getId());
        dto.setFieldName(request.getFieldName());
        dto.setOldValue(request.getOldValue());
        dto.setNewValue(request.getNewValue());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
    
    private void applyProfileChange(User user, String fieldName, String newValue) {
        switch (fieldName) {
            case "firstName": user.setFirstName(newValue); break;
            case "lastName": user.setLastName(newValue); break;
            case "phoneNumber": user.setPhoneNumber(newValue); break;
            case "profilePictureUrl": user.setProfilePictureUrl(newValue); break;
            default: throw new RuntimeException("Unknown field: " + fieldName);
        }
    }
    
    @Override
    public User register(User user) {
        return null;
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
