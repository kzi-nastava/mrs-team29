package service.impl;

import dto.ride.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import domain.entities.*;
import domain.enums.*;
import service.RideService;
import repository.*;

public class RideServiceImpl implements RideService {

	private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
	
    public RideServiceImpl(RideRepository rideRepository,
            UserRepository userRepository,
            DriverRepository driverRepository) {
    	this.rideRepository = rideRepository;
    	this.userRepository = userRepository;
    	this.driverRepository = driverRepository;
    }
    	
    @Override
    public RideResponseDTO orderRide(RideOrderDTO dto) {

    	 User creator = userRepository.findById(dto.getCreatorId())
                 .orElseThrow(() -> new RuntimeException("User not found"));

         boolean hasActiveRide = rideRepository
                 .existsByPassengerIdAndStatusIn(
                         creator.getId(),
                         List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.ACTIVE)
                 );

         if (hasActiveRide) {
             throw new RuntimeException("User already has an active ride");
         }

         Ride ride = new Ride();
         ride.setId(UUID.randomUUID().toString());
         ride.setPickupAddress(dto.getPickupAddress());
         ride.setDestinationAddress(dto.getDestinationAddress());
         ride.setStops(dto.getStops());
         ride.setPassengers(List.of(creator));
         ride.setStatus(RideStatus.REQUESTED);

         ride.setPrice(1000);

         ride.setTimestamps(List.of(LocalDateTime.now()));

         Driver driver = driverRepository.findFirstAvailableDriver()
                 .orElseThrow(() -> new RuntimeException("No available drivers"));

         ride.setDriver(driver);

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
    public RideResponseDTO orderRideFromFavorite(
            String favoriteRouteId,
            FavoriteRideOrderDTO dto
    ) {
        RideResponseDTO response = new RideResponseDTO();

        response.setRideId(UUID.randomUUID().toString());
        response.setStatus(RideStatus.REQUESTED);
        response.setPrice(700.0); 

        return response;
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

