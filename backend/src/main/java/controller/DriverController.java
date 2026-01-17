package controller;

import dto.driver.*;
import service.*;

import domain.entities.Driver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/register")
    public Driver registerDriver(@RequestBody DriverRegistrationDTO dto) {
        return driverService.registerDriver(dto);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveDriverDTO>> getActiveDrivers() {
        List<ActiveDriverDTO> drivers = driverService.getActiveDrivers();
        return ResponseEntity.ok(drivers);
    }
}

