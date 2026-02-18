package service.impl;

import dto.auth.*;
import domain.entities.*;
import domain.enums.*;
import repository.*;
import service.AuthService;
import service.EmailService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final RideRepository rideRepository;
    private final EmailService emailService;
    
    private static final String DEFAULT_PROFILE_PICTURE = "https://via.placeholder.com/150";
    
    public AuthServiceImpl(
            UserRepository userRepository,
            DriverRepository driverRepository,
            ActivationTokenRepository activationTokenRepository,
            RideRepository rideRepository,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.rideRepository = rideRepository;
        this.emailService = emailService;
    }
    
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
            // Try to find driver by user ID
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
        response.setToken("mock-jwt-token-" + UUID.randomUUID()); // Mock token for now
        
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
        boolean hasActiveRides = rideRepository.existsByDriver_IdAndStatusIn(driverId,
            List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.IN_PROGRESS));
        return !hasActiveRides;
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
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserType(UserType.CLIENT); // Default to client (passenger), can upgrade to driver later
        user.setUserName(dto.getEmail()); // Use email as username for now
        user.setIsActive(false); // User account not yet activated
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
        
        String fullName = user.getFirstName() + " " + user.getLastName();
        emailService.sendActivationEmail(user.getEmail(), fullName, token.getToken());
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
        
        // TODO: Send password reset email
        // emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
        System.out.println("Password reset link: http://localhost:4200/reset-password?token=" + token.getToken());
    }
    
    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO dto) {
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
}
