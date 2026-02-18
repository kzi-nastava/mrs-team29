package controller;

import dto.ride.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RideService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "http://localhost:4200")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    // ============ CLIENT ENDPOINTS ============
    
    @PostMapping
    public ResponseEntity<?> orderRide(@Valid @RequestBody RideOrderDTO dto) {
        // Check if user already has an active ride
        if (rideService.hasActiveRide(dto.getCreatorId())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "You already have an active ride. Please finish it before ordering a new one."));
        }

        try {
            RideResponseDTO response = rideService.orderRide(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/favorites/{favoriteRouteId}")
    public ResponseEntity<RideResponseDTO> orderFromFavorite(
            @PathVariable String favoriteRouteId,
            @Valid @RequestBody FavoriteRideOrderDTO dto
    ) {
        RideResponseDTO response = rideService.orderRideFromFavorite(favoriteRouteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Boolean> hasActiveRide(@PathVariable String userId) {
        return ResponseEntity.ok(rideService.hasActiveRide(userId));
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RideResponseDTO>> getUserRideHistory(@PathVariable String userId) {
        return ResponseEntity.ok(rideService.getUserRideHistory(userId));
    }

    // ============ DRIVER ENDPOINTS ============
    
    @GetMapping("/driver/{driverId}/current")
    public ResponseEntity<?> getDriverCurrentRide(@PathVariable String driverId) {
        try {
            RideResponseDTO response = rideService.getDriverCurrentRide(driverId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if ("No active ride found for driver".equals(e.getMessage())) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{rideId}/start")
    public ResponseEntity<?> startRide(
            @PathVariable String rideId,
            @RequestParam String driverId
    ) {
        try {
            RideResponseDTO response = rideService.startRide(rideId, driverId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<?> finishRide(
            @PathVariable String rideId,
            @RequestParam String driverId
    ) {
        try {
            RideResponseDTO response = rideService.finishRide(rideId, driverId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<List<RideResponseDTO>> getDriverRideHistory(
            @PathVariable String driverId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        java.time.LocalDate start = startDate != null ? java.time.LocalDate.parse(startDate) : null;
        java.time.LocalDate end = endDate != null ? java.time.LocalDate.parse(endDate) : null;
        return ResponseEntity.ok(rideService.getDriverRideHistory(driverId, start, end));
    }
}
