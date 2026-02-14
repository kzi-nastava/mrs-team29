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
import service.EmailService;
import utils.AddressParser;
import repository.*;

@Service
public class DriverServiceImpl implements DriverService {

	private final DriverRepository driverRepository;
    private final AddressRepository addressRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;

    public DriverServiceImpl(DriverRepository driverRepository,
                             AddressRepository addressRepository,
                             ActivationTokenRepository activationTokenRepository,
                             EmailService emailService) {
        this.driverRepository = driverRepository;
        this.addressRepository = addressRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.emailService = emailService;
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
        if (dto.getAddress() != null) {
            Address address = dto.getAddress();
            if (isBlank(address.getStreetNumber()) && !isBlank(address.getStreet())) {
                address = AddressParser.parseAddressLine(address.getStreet());
            }
            if (isBlank(address.getStreet()) || isBlank(address.getStreetNumber())) {
                throw new RuntimeException("Address must include street and number");
            }
            addressRepository.save(address);
            driver.setAddress(address);
        }
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

        // Send activation email to driver
        String fullName = driver.getFirstName() + " " + driver.getLastName();
        emailService.sendActivationEmail(driver.getEmail(), fullName, token.getToken());

        System.out.println("Driver activation email sent to " + driver.getEmail());

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
		// TODO: Implement when ride history is fully integrated
		return List.of();
	}
	
	@Override
	public double getWorkingHoursLast24h(String driverId) {
		// TODO: Calculate from actual ride data when integrated
		// For now return mock data
		return 6.5;
	}

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
