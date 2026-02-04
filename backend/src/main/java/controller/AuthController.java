package controller;

import dto.ApiResponse;
import dto.user.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    // 2.2.1 - Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            System.out.println("[AuthController] Login attempt for email=" + dto.getEmail());
            LoginResponseDTO response = userService.login(dto);
            System.out.println("[AuthController] Login success for email=" + dto.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (RuntimeException e) {
            System.out.println("[AuthController] Login failed for email=" + dto.getEmail() + " reason=" + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // 2.2.1 - Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String userId) {
        try {
            userService.logout(userId);
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // 2.2.1 - Set driver status (active/inactive)
    @PostMapping("/driver/{driverId}/status")
    public ResponseEntity<?> setDriverStatus(
            @PathVariable String driverId,
            @Valid @RequestBody DriverStatusDTO dto) {
        try {
            userService.setDriverStatus(driverId, dto.getActive());
            return ResponseEntity.ok(ApiResponse.success(
                    "Driver status updated to " + (dto.getActive() ? "active" : "inactive"), null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // 2.2.1 - Check if driver can logout
    @GetMapping("/driver/{driverId}/can-logout")
    public ResponseEntity<?> canDriverLogout(@PathVariable String driverId) {
        try {
            boolean canLogout = userService.canDriverLogout(driverId);
            return ResponseEntity.ok(ApiResponse.success(
                    canLogout ? "Can logout" : "Cannot logout - active ride in progress", canLogout));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // 2.2.2 - User registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO dto) {
        try {
            System.out.println("[AuthController] Register attempt for email=" + dto.getEmail());
            userService.registerUser(dto);
            System.out.println("[AuthController] Register success for email=" + dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Registration successful. Please check your email to activate your account.", null));
        } catch (RuntimeException e) {
            System.out.println("[AuthController] Register failed for email=" + dto.getEmail() + " reason=" + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // 2.2.2 - Account activation
    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String token) {
        try {
            userService.activateAccount(token);
            return ResponseEntity.ok(ApiResponse.success(
                    "Account activated successfully. You can now login.", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Password reset request
    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO dto) {
        try {
            userService.requestPasswordReset(dto.getEmail());
            return ResponseEntity.ok(ApiResponse.success(
                    "Password reset link sent to your email.", null));
        } catch (RuntimeException e) {
            // Don't reveal if email exists or not for security
            return ResponseEntity.ok(ApiResponse.success(
                    "If an account with that email exists, a password reset link has been sent.", null));
        }
    }
    
    // Password reset with token
    @PostMapping("/password-reset/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetTokenDTO dto) {
        try {
            userService.resetPasswordWithToken(dto);
            return ResponseEntity.ok(ApiResponse.success(
                    "Password reset successfully. You can now login with your new password.", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
