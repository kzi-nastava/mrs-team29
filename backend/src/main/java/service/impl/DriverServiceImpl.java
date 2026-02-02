package service.impl;

import dto.driver.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import domain.entities.*;
import domain.enums.*;
import service.DriverService;
import repository.*;

@Service
public class DriverServiceImpl implements DriverService {

	private final DriverRepository driverRepository;
    private final ActivationTokenRepository activationTokenRepository;

    public DriverServiceImpl(DriverRepository driverRepository,
                             ActivationTokenRepository activationTokenRepository) {
        this.driverRepository = driverRepository;
        this.activationTokenRepository = activationTokenRepository;
    }
	
	
    @Override
    public Driver registerDriver(DriverRegistrationDTO dto) {

        Vehicle vehicle = dto.toVehicle();

        Driver driver = new Driver();
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setGender(dto.getGender());
        driver.setUserName(dto.getUsername());
        driver.setEmail(dto.getEmail());
        driver.setPassword(dto.getPassword());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setAddress(dto.getAddress());
        driver.setProfilePictureUrl(dto.getProfilePictureUrl());

        driver.setVehicle(vehicle);
        driver.setUserType(UserType.DRIVER);
        driver.setIsActive(false);
        driver.setIsBlocked(false);
        driver.setStatus(DriverStatus.INACTIVE);

        driverRepository.save(driver);

        ActivationToken token = new ActivationToken(
                driver,
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(24),
                false
        );

        activationTokenRepository.save(token);

        return driver;
    }

    
    @Override
    public List<ActiveDriverDTO> getActiveDrivers() {

        // Stub response – No database
        List<ActiveDriverDTO> result = new ArrayList<>();

        result.add(new ActiveDriverDTO(
                "driver-1",
                "vehicle-1",
                45.2671,
                19.8335,
                false
        ));

        result.add(new ActiveDriverDTO(
                "driver-2",
                "vehicle-2",
                45.2550,
                19.8450,
                true
        ));

        return result;
    }

    @Override
    public void activateDriver(String tokenValue, String newPassword) {

        ActivationToken token = activationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        Driver driver = driverRepository.findById(token.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setPassword(newPassword);
        driver.setIsActive(true);
        driver.setStatus(DriverStatus.AVAILABLE);

        driverRepository.save(driver);
        activationTokenRepository.delete(token);
    }
    
	@Override
	public Driver getAvailableDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStatus(String driverId, DriverStatus status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<DriverRideHistoryDTO> getDriverRideHistory(String driverId, LocalDate from, LocalDate to) {
		// TODO Auto-generated method stub
		return List.of();
	}
}
