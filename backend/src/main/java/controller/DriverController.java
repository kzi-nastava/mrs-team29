package controller;

import dto.driver.DriverRegistrationDTO;

import domain.entities.Driver;

import org.springframework.web.bind.annotation.*;
import service.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * 2.2.3 – Driver registration
     */
    @PostMapping("/register")
    public Driver registerDriver(@RequestBody DriverRegistrationDTO dto) {
        return driverService.registerDriver(dto);
    }
}

