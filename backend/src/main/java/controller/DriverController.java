package controller;

import dto.driver.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.DriverService;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(@Valid @RequestBody DriverRegistrationDTO dto) {
        driverService.registerDriver(dto);
        return ResponseEntity.ok("Driver successfully registered");
    }


    @GetMapping("/active")
    public ResponseEntity<List<ActiveDriverDTO>> getActiveDrivers() {
        List<ActiveDriverDTO> drivers = driverService.getActiveDrivers();
        return ResponseEntity.ok(drivers);
    }
    
    @GetMapping("/{driverId}/rides/history")
    public List<DriverRideHistoryDTO> getDriverRideHistory(
            @PathVariable String driverId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return driverService.getDriverRideHistory(driverId, from, to);
    }
}

