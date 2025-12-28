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
}
