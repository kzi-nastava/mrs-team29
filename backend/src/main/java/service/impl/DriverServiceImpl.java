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
@SuppressWarnings("null")
public class DriverServiceImpl implements DriverService {

	private final DriverRepository driverRepository;
    private final AddressRepository addressRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;
    private final RideRepository rideRepository;

    public DriverServiceImpl(DriverRepository driverRepository,
                             AddressRepository addressRepository,
                             ActivationTokenRepository activationTokenRepository,
                             EmailService emailService,
                             RideRepository rideRepository) {
        this.driverRepository = driverRepository;
        this.addressRepository = addressRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.emailService = emailService;
        this.rideRepository = rideRepository;
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
        String initialPassword = (dto.getPassword() == null || dto.getPassword().isBlank())
            ? UUID.randomUUID().toString()
            : dto.getPassword();
        driver.setPassword(initialPassword);
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
        emailService.sendDriverActivationEmail(driver.getEmail(), fullName, token.getToken());

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

        if (token.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        Driver driver = driverRepository.findById(token.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setPassword(newPassword);
        driver.setIsActive(true);
        driver.setStatus(DriverStatus.AVAILABLE);

        driverRepository.save(driver);

        token.setUsed(true);
        activationTokenRepository.save(token);
        activationTokenRepository.delete(token);
    }
    
	@Override
	public Driver getAvailableDriver() {
		return driverRepository.findFirstByStatusAndIsActiveTrueAndIsBlockedFalseOrderByIdAsc(
				DriverStatus.AVAILABLE)
				.orElse(null);
	}

	@Override
	public void updateStatus(String driverId, DriverStatus status) {
		Driver driver = driverRepository.findById(driverId)
				.orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
		
		driver.setStatus(status);
		driverRepository.save(driver);
	}

	@Override
	public List<DriverRideHistoryDTO> getDriverRideHistory(String driverId, LocalDate from, LocalDate to) {
		LocalDateTime startDateTime = (from != null) ? from.atStartOfDay() : null;
		LocalDateTime endDateTime = (to != null) ? to.atTime(23, 59, 59) : null;
		
		List<Ride> rides = rideRepository.findDriverRideHistoryByDateRange(
				driverId, RideStatus.FINISHED, startDateTime, endDateTime);
		
		return rides.stream()
				.map(this::mapToDriverRideHistoryDTO)
				.toList();
	}
	
	@Override
	public double getWorkingHoursLast24h(String driverId) {
		// TODO: Calculate from actual ride data when integrated
		// For now return mock data
		return 6.5;
	}
	
	private DriverRideHistoryDTO mapToDriverRideHistoryDTO(Ride ride) {
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		
		if (ride.getTimestamps() != null && !ride.getTimestamps().isEmpty()) {
			if (ride.getTimestamps().size() > 1) {
				startTime = ride.getTimestamps().get(1); // Started time
			}
			if (ride.getTimestamps().size() > 2) {
				endTime = ride.getTimestamps().get(2); // Finished time
			}
		}
		
		String startLocation = formatAddress(ride.getPickupAddress());
		String endLocation = formatAddress(ride.getDestinationAddress());
		
		List<PassengerInfoDTO> passengers = ride.getPassengers().stream()
				.map(user -> new PassengerInfoDTO(
						user.getId(),
						user.getFirstName() + " " + user.getLastName(),
						user.getEmail()
				))
				.toList();
		
		boolean canceled = ride.getStatus() == RideStatus.CANCELED_BY_DRIVER;
		
		return new DriverRideHistoryDTO(
				ride.getId(),
				startTime,
				endTime,
				startLocation,
				endLocation,
				canceled,
				null, // canceledBy - would need additional data to determine
				ride.getPrice(),
				false, // panicActivated - would need additional data to determine
				passengers
		);
	}
	
	private String formatAddress(Address address) {
		if (address == null) {
			return "N/A";
		}
		StringBuilder sb = new StringBuilder();
		if (address.getStreet() != null) {
			sb.append(address.getStreet());
		}
		if (address.getStreetNumber() != null) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(address.getStreetNumber());
		}
		if (address.getCity() != null) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(address.getCity());
		}
		return sb.toString();
	}

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
