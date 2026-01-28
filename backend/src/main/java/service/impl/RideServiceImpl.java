package service.impl;

import dto.ride.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import domain.entities.*;
import domain.enums.*;
import service.RideService;
import repository.*;

@Service
public class RideServiceImpl implements RideService {

	private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final FavoriteRouteRepository favoriteRouteRepository;
    private final AddressRepository addressRepository;
	
    public RideServiceImpl(
            RideRepository rideRepository,
            UserRepository userRepository,
            DriverRepository driverRepository,
            AddressRepository addressRepository,
            FavoriteRouteRepository favoriteRouteRepository
    ) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.addressRepository = addressRepository;
        this.favoriteRouteRepository = favoriteRouteRepository;
    }
    	
    @Override
    public RideResponseDTO orderRide(RideOrderDTO dto) {

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasActiveRide = rideRepository.existsByPassengerIdAndStatusIn(
                creator.getId(),
                List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.ACTIVE)
        );

        if (hasActiveRide) {
            throw new RuntimeException("User already has active ride");
        }

        Address pickup = addressRepository.findById(dto.getPickupAddressId())
                .orElseThrow(() -> new RuntimeException("Pickup address not found"));

        Address destination = addressRepository.findById(dto.getDestinationAddressId())
                .orElseThrow(() -> new RuntimeException("Destination address not found"));

        List<Address> stops = dto.getStopAddressIds() == null ? new ArrayList<>() :
                dto.getStopAddressIds().stream()
                        .map(id -> addressRepository.findById(id).orElseThrow())
                        .collect(Collectors.toList());

        Driver driver = driverRepository.findFirstAvailableDriver()
                .orElseThrow(() -> new RuntimeException("No available drivers"));

        Ride ride = new Ride();
        ride.setId(UUID.randomUUID().toString());
        ride.setPickupAddress(pickup);
        ride.setDestinationAddress(destination);
        ride.setStops(stops);
        ride.setPassengers(List.of(creator));
        ride.setDriver(driver);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setPrice(1000);
        ride.setTimestamps(List.of(LocalDateTime.now()));

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }
    
    @Override
    public RideResponseDTO startRide(String rideId, String driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Driver not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new RuntimeException("Ride cannot be started");
        }

        ride.setStatus(RideStatus.ACTIVE);
        ride.getTimestamps().add(LocalDateTime.now());

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }

    @Override
    public RideResponseDTO finishRide(String rideId, String driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Driver not assigned");
        }

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new RuntimeException("Ride is not active");
        }

        ride.setStatus(RideStatus.FINISHED);
        ride.getTimestamps().add(LocalDateTime.now());

        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);

        rideRepository.save(ride);
        driverRepository.save(driver);

        return RideResponseDTO.fromRide(ride);
    }
    
    @Override
    public RideResponseDTO orderRideFromFavorite(String favoriteRouteId, FavoriteRideOrderDTO dto) {

        FavoriteRoute route = favoriteRouteRepository.findById(favoriteRouteId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        User creator = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Driver driver = driverRepository.findFirstAvailableDriver()
                .orElseThrow(() -> new RuntimeException("No available drivers"));

        Ride ride = new Ride();
        ride.setId(UUID.randomUUID().toString());
        ride.setPickupAddress(route.getPickupAddress());
        ride.setDestinationAddress(route.getDestinationAddress());
        ride.setStops(route.getStops());
        ride.setPassengers(List.of(creator));
        ride.setDriver(driver);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setPrice(700);
        ride.setTimestamps(List.of(LocalDateTime.now()));

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }

    @Override
    public RideStartResponseDTO startRide(String rideId) {

        return new RideStartResponseDTO(
                rideId,
                RideStatus.ACTIVE,
                LocalDateTime.now()
        );
    }
    
    @Override
    public RideTrackingDTO getRideTracking(String rideId) {

        // Stub response
        return new RideTrackingDTO(
                rideId,
                45.2675,
                19.8339,
                8
        );
    }

    @Override
    public void reportInconsistency(RideInconsistencyReportDTO dto) {
        // Stub – Later saving into database
        System.out.println("Inconsistency reported for ride " + dto.getRideId());
    }
    
    @Override
    public RideFinishResponseDTO finishRideResponse(RideFinishDTO dto) {

        // Stub logic:
        // - Ride Complete
        // - Paid
        // - Driver Available
        // - No next Reserved Ride

        return new RideFinishResponseDTO(
                "AVAILABLE",
                null
        );
    }
    
	@Override
	public Ride createRide(Ride ride) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelRide(String rideId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Ride getRidebyId(String rideId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Ride> getActiveRides() {
		// TODO Auto-generated method stub
		return null;
	}

}

