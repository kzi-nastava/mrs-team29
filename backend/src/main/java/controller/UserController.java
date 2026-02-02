package controller;

import dto.user.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserProfile(@PathVariable String id) {
        return userService.getUserProfile(id);
    }

    @PutMapping("/{id}")
    public UserProfileDTO updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserProfileDTO dto
    ) {
        return userService.updateProfile(id, dto);
    }
    
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable String id,
            @Valid @RequestBody PasswordResetDTO dto) {
        
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        
        userService.changePassword(id, dto);
        return ResponseEntity.ok("Password changed successfully");
    }
    
    @GetMapping("/{id}/profile-change-requests")
    public ResponseEntity<List<ProfileChangeRequestDTO>> getProfileChangeRequests(@PathVariable String id) {
        List<ProfileChangeRequestDTO> requests = userService.getProfileChangeRequests(id);
        return ResponseEntity.ok(requests);
    }
}

