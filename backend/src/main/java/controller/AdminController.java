package controller;

import dto.user.ProfileChangeRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
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
}
