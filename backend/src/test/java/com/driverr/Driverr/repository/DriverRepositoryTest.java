package com.driverr.Driverr.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import domain.entities.*;
import domain.enums.*;
import main.DriverrApplication;
import repository.DriverRepository;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = main.DriverrApplication.class)
class DriverRepositoryTest {
    // Note: Spring will use application.properties configuration for test DB

    @Autowired
    private DriverRepository driverRepository;

    private String activeDriverId;
    private String blockedDriverId;

    @BeforeEach
    void setUp() {
        Driver active = buildDriver("active-" + UUID.randomUUID(), "active@test.com");
        active.setStatus(DriverStatus.AVAILABLE);
        active.setIsActive(true);
        active.setIsBlocked(false);
        active = driverRepository.save(active);
        activeDriverId = active.getId();

        Driver blocked = buildDriver("blocked-" + UUID.randomUUID(), "blocked@test.com");
        blocked.setStatus(DriverStatus.AVAILABLE);
        blocked.setIsActive(true);
        blocked.setIsBlocked(true);
        blocked = driverRepository.save(blocked);
        blockedDriverId = blocked.getId();
    }

    @AfterEach
    void tearDown() {
        if (activeDriverId != null) {
            driverRepository.deleteById(activeDriverId);
        }
        if (blockedDriverId != null) {
            driverRepository.deleteById(blockedDriverId);
        }
    }

    @Test
    void findAvailableDrivers_excludesBlockedAndInactive() {
        List<Driver> available = driverRepository.findAvailableDrivers(DriverStatus.AVAILABLE);
        assertFalse(available.isEmpty());
        assertTrue(available.stream().allMatch(d -> d.getIsActive() && !d.getIsBlocked()));
    }

    @Test
    void findFirstByStatusAndIsActiveTrueAndIsBlockedFalseOrderByIdAsc_returnsActiveDriver() {
        var result = driverRepository.findFirstByStatusAndIsActiveTrueAndIsBlockedFalseOrderByIdAsc(
            DriverStatus.AVAILABLE
        );
        assertTrue(result.isPresent());
        assertTrue(result.get().getIsActive());
        assertFalse(result.get().getIsBlocked());
        assertEquals(DriverStatus.AVAILABLE, result.get().getStatus());
    }

    private Driver buildDriver(String userName, String email) {
        Driver driver = new Driver();
        driver.setFirstName("Test");
        driver.setLastName("Driver");
        driver.setUserName(userName);
        driver.setEmail(email.replace("@", "+" + UUID.randomUUID() + "@"));
        driver.setPassword("pass");
        driver.setUserType(UserType.DRIVER);
        driver.setIsActive(true);
        driver.setIsBlocked(false);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel("Model X");
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setRegistrationPlate("TEST-" + UUID.randomUUID().toString().substring(0, 6));
        vehicle.setSeats(4);
        vehicle.setPetsAllowed(false);
        vehicle.setBabiesAllowed(false);
        driver.setVehicle(vehicle);

        return driver;
    }
}
