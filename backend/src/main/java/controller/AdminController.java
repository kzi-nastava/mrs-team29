package controller;

import domain.enums.VehicleType;
import dto.admin.UpdateVehiclePricingDTO;
import dto.admin.VehiclePricingDTO;
import dto.user.BlockUserDTO;
import dto.user.ProfileChangeRequestDTO;
import dto.user.UserBlockStatusDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RidePricingService;
import service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final UserService userService;
    private final RidePricingService ridePricingService;

    public AdminController(UserService userService, RidePricingService ridePricingService) {
        this.userService = userService;
        this.ridePricingService = ridePricingService;
    }

    @GetMapping("/profile-change-requests")
    public ResponseEntity<List<ProfileChangeRequestDTO>> getAllPendingRequests() {
        List<ProfileChangeRequestDTO> requests = userService.getAllPendingProfileChangeRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/profile-change-requests/{requestId}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable String requestId) {
        userService.approveProfileChangeRequest(requestId);
        return ResponseEntity.ok(Map.of("message", "Request approved successfully", "status", "success"));
    }

    @PostMapping("/profile-change-requests/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable String requestId) {
        userService.rejectProfileChangeRequest(requestId);
        return ResponseEntity.ok(Map.of("message", "Request rejected successfully", "status", "success"));
    }
    
    // Blocking endpoints
    @PostMapping("/users/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable String userId, @RequestBody BlockUserDTO dto) {
        userService.blockUser(userId, dto.blockNote());
        return ResponseEntity.ok(Map.of("message", "User blocked successfully", "status", "success"));
    }
    
    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable String userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok(Map.of("message", "User unblocked successfully", "status", "success"));
    }
    
    @GetMapping("/users/{userId}/block-status")
    public ResponseEntity<UserBlockStatusDTO> getUserBlockStatus(@PathVariable String userId) {
        UserBlockStatusDTO status = userService.getUserBlockStatus(userId);
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/users/block-status")
    public ResponseEntity<List<UserBlockStatusDTO>> getAllUsersBlockStatus() {
        List<UserBlockStatusDTO> statuses = userService.getAllUsersBlockStatus();
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/pricing")
    public ResponseEntity<List<VehiclePricingDTO>> getAllPricing() {
        return ResponseEntity.ok(ridePricingService.getAllPricing());
    }

    @PutMapping("/pricing/{vehicleType}")
    public ResponseEntity<?> updatePricing(
        @PathVariable VehicleType vehicleType,
        @Valid @RequestBody UpdateVehiclePricingDTO dto
    ) {
        try {
            return ResponseEntity.ok(ridePricingService.updatePricing(vehicleType, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
