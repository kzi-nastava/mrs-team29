package controller;

import dto.driver.*;
import dto.ApiResponse;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.DriverService;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(@Valid @RequestBody DriverRegistrationDTO dto) {
        try {
            driverService.registerDriver(dto);
            return ResponseEntity.ok(ApiResponse.success("Driver successfully registered. Activation email sent.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
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
    
    @PostMapping("/activate")
    public ResponseEntity<?> activateDriver(@Valid @RequestBody DriverActivationDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Passwords do not match"));
        }
        try {
            driverService.activateDriver(dto.getToken(), dto.getPassword());
            return ResponseEntity.ok(ApiResponse.success("Driver activated successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverDetailsDTO> getDriverById(@PathVariable String driverId) {
        return ResponseEntity.ok(DriverDetailsDTO.fromDriver(driverService.getDriverById(driverId)));
    }
    
    @GetMapping("/{driverId}/working-hours")
    public ResponseEntity<Double> getWorkingHours(@PathVariable String driverId) {
        double hours = driverService.getWorkingHoursLast24h(driverId);
        return ResponseEntity.ok(hours);
    }
}
