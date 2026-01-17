package controller;

import dto.ride.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RideService;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }
    @PostMapping
    public ResponseEntity<RideResponseDTO> orderRide(@RequestBody RideOrderDTO dto) {
        RideResponseDTO response = rideService.orderRide(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PatchMapping("/{rideId}/start")
    public ResponseEntity<RideStartResponseDTO> startRide(
            @PathVariable String rideId
    ) {
        RideStartResponseDTO response = rideService.startRide(rideId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{rideId}/tracking")
    public ResponseEntity<RideTrackingDTO> getRideTracking(
            @PathVariable String rideId) {

        RideTrackingDTO tracking = rideService.getRideTracking(rideId);
        return ResponseEntity.ok(tracking);
    }

    @PostMapping("/{rideId}/inconsistency")
    public ResponseEntity<Void> reportInconsistency(
            @PathVariable String rideId,
            @RequestBody RideInconsistencyReportDTO dto) {

        dto.setRideId(rideId);
        rideService.reportInconsistency(dto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{rideId}/finish")
    public ResponseEntity<RideFinishResponseDTO> finishRide(
            @PathVariable String rideId,
            @RequestBody RideFinishDTO dto) {

        dto.setRideId(rideId);

        RideFinishResponseDTO response = rideService.finishRide(dto);

        return ResponseEntity.ok(response);
    }
}
