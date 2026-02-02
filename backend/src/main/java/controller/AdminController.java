package controller;

import dto.user.ProfileChangeRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
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
        return ResponseEntity.ok("Request approved");
    }

    @PostMapping("/profile-change-requests/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable String requestId) {
        userService.rejectProfileChangeRequest(requestId);
        return ResponseEntity.ok("Request rejected");
    }
}
