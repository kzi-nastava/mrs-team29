package service.impl;

import dto.ride.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import domain.entities.*;
import domain.enums.*;
import service.RideService;
import repository.*;

@Service
@SuppressWarnings("null")
public class RideServiceImpl implements RideService {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Belgrade");

    private LocalDateTime nowInAppZone() {
        return LocalDateTime.now(APP_ZONE);
    }

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

        boolean hasActiveRide = rideRepository.existsByPassengers_IdAndStatusIn(
                creator.getId(),
                List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.IN_PROGRESS)
        );

        if (hasActiveRide) {
            throw new RuntimeException("User already has active ride");
        }
        
        // Validate scheduled time (max 5 hours in future)
        if (dto.getScheduledTime() != null) {
            LocalDateTime now = nowInAppZone();
            LocalDateTime maxScheduleTime = now.plusHours(5);
            
            if (dto.getScheduledTime().isBefore(now)) {
                throw new RuntimeException("Cannot schedule rides in the past");
            }
            if (dto.getScheduledTime().isAfter(maxScheduleTime)) {
                throw new RuntimeException("Cannot schedule rides more than 5 hours in advance");
            }
        }

        Address pickup = addressRepository.findById(dto.getPickupAddressId())
                .orElseThrow(() -> new RuntimeException("Pickup address not found"));

        Address destination = addressRepository.findById(dto.getDestinationAddressId())
                .orElseThrow(() -> new RuntimeException("Destination address not found"));

        List<Address> stops = dto.getStopAddressIds() == null ? new ArrayList<>() :
                dto.getStopAddressIds().stream()
                        .map(id -> addressRepository.findById(id).orElseThrow())
                        .collect(Collectors.toList());
        
        // Collect passengers (creator + linked passengers)
        List<User> passengers = new ArrayList<>();
        passengers.add(creator);
        
        if (dto.getPassengerIds() != null && !dto.getPassengerIds().isEmpty()) {
            List<User> linkedPassengers = dto.getPassengerIds().stream()
                .map(ref -> userRepository.findById(ref)
                    .or(() -> userRepository.findByEmail(ref))
                    .orElseThrow(() -> new RuntimeException("Passenger not found: " + ref)))
                .collect(Collectors.toList());
            passengers.addAll(linkedPassengers);
        }

        Driver driver = driverRepository.findFirstByStatusInAndIsActiveTrueAndIsBlockedFalseOrderByIdAsc(
            List.of(DriverStatus.AVAILABLE, DriverStatus.ACTIVE)
        )
                .orElseThrow(() -> new RuntimeException("No available drivers"));

        // Assign driver and update their status
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        Ride ride = new Ride();
        ride.setPickupAddress(pickup);
        ride.setDestinationAddress(destination);
        ride.setStops(stops);
        ride.setPassengers(passengers);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setScheduledTime(dto.getScheduledTime());
        
        // Calculate price: base price by vehicle type + 120 per km
        double basePrice = calculateBasePriceByVehicleType(dto.getVehicleType());
        double distanceKm = calculateDistance(pickup, destination);
        ride.setPrice(basePrice + (distanceKm * 120));
        
        ride.setTimestamps(List.of(nowInAppZone()));

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }
    
    private double calculateBasePriceByVehicleType(VehicleType type) {
        if (type == null) return 300; // Standard
        switch (type) {
            case LUXURY: return 500;
            case VAN: return 400;
            default: return 300; // STANDARD
        }
    }
    
    @Override
    public RideResponseDTO startRide(String rideId, String driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getDriver() == null) {
            throw new RuntimeException("Ride has no assigned driver");
        }

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Driver not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new RuntimeException("Ride cannot be started. Current status: " + ride.getStatus());
        }

        LocalDateTime now = nowInAppZone();
        if (ride.getScheduledTime() != null && now.isBefore(ride.getScheduledTime().minusMinutes(1))) {
            throw new RuntimeException("Ride cannot be started before scheduled time");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        List<LocalDateTime> timestamps = ride.getTimestamps() == null
            ? new ArrayList<>()
            : new ArrayList<>(ride.getTimestamps());
        timestamps.add(nowInAppZone());
        ride.setTimestamps(timestamps);

        // Update driver status
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.ACTIVE);
        driverRepository.save(driver);

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }

    @Override
    public RideResponseDTO finishRide(String rideId, String driverId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getDriver() == null) {
            throw new RuntimeException("Ride has no assigned driver");
        }

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Driver not assigned to this ride");
        }

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new RuntimeException("Ride is not in progress. Current status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.FINISHED);
        List<LocalDateTime> timestamps = ride.getTimestamps() == null
            ? new ArrayList<>()
            : new ArrayList<>(ride.getTimestamps());
        timestamps.add(nowInAppZone());
        ride.setTimestamps(timestamps);

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

        Driver driver = driverRepository.findFirstByStatusInAndIsActiveTrueAndIsBlockedFalseOrderByIdAsc(
            List.of(DriverStatus.AVAILABLE, DriverStatus.ACTIVE)
        )
                .orElseThrow(() -> new RuntimeException("No available drivers"));

        // Assign driver and update their status
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);

        Ride ride = new Ride();
        ride.setPickupAddress(route.getPickupAddress());
        ride.setDestinationAddress(route.getDestinationAddress());
        ride.setStops(route.getStops());
        ride.setPassengers(List.of(creator));
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setPrice(700);
        ride.setTimestamps(List.of(nowInAppZone()));

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }

    @Override
    public RideResponseDTO getDriverCurrentRide(String driverId) {
        List<Ride> activeRides = rideRepository.findByDriver_IdAndStatusIn(
            driverId,
            List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.IN_PROGRESS)
        );

        LocalDateTime now = nowInAppZone();

        List<Ride> visibleRides = activeRides.stream()
            .filter(ride -> {
                if (ride.getStatus() == RideStatus.IN_PROGRESS) {
                    return true;
                }

                if (ride.getScheduledTime() == null) {
                    return true;
                }

                return !ride.getScheduledTime().isAfter(now.plusMinutes(1));
            })
            .collect(Collectors.toList());

        if (visibleRides.isEmpty()) {
            throw new RuntimeException("No active ride found for driver");
        }

        Ride currentRide = visibleRides.stream()
            .max(Comparator.comparing(
                Ride::getScheduledTime,
                Comparator.nullsLast(Comparator.naturalOrder())
            ))
            .orElse(visibleRides.get(0));

        return RideResponseDTO.fromRide(currentRide);
    }

    @Override
    public List<RideResponseDTO> getDriverRideHistory(String driverId) {
        return getDriverRideHistory(driverId, null, null);
    }
    
    @Override
    public List<RideResponseDTO> getDriverRideHistory(String driverId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        java.time.LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<Ride> history = rideRepository.findByDriver_IdAndStatusOrderByTimestampsDesc(
                driverId,
                RideStatus.FINISHED
        );

        return history.stream()
                .filter(ride -> {
                    LocalDateTime finishedAt = getRideFinishedAt(ride);
                    if (finishedAt == null) {
                        return startDateTime == null && endDateTime == null;
                    }
                    if (startDateTime != null && finishedAt.isBefore(startDateTime)) {
                        return false;
                    }
                    if (endDateTime != null && finishedAt.isAfter(endDateTime)) {
                        return false;
                    }
                    return true;
                })
                .map(RideResponseDTO::fromRide)
                .collect(Collectors.toList());
    }

    private LocalDateTime getRideFinishedAt(Ride ride) {
        if (ride.getTimestamps() == null || ride.getTimestamps().isEmpty()) {
            return null;
        }
        return ride.getTimestamps().get(ride.getTimestamps().size() - 1);
    }

    @Override
    public List<RideResponseDTO> getUserRideHistory(String userId) {
        List<Ride> history = rideRepository.findUserRideHistory(userId, RideStatus.FINISHED);

        return history.stream()
                .map(RideResponseDTO::fromRide)
                .collect(Collectors.toList());
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
	
	@Override
	public List<FavoriteRouteDTO> getUserFavoriteRoutes(String userId) {
        List<FavoriteRoute> routes = favoriteRouteRepository.findByUser_Id(userId);
	    return routes.stream()
	        .map(this::mapFavoriteRouteToDTO)
	        .collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public FavoriteRouteDTO createFavoriteRoute(FavoriteRouteDTO dto) {
	    User user = userRepository.findById(dto.getUserId())
	        .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    Address pickup = addressRepository.findById(dto.getPickupAddressId())
	        .orElseThrow(() -> new RuntimeException("Pickup address not found"));
	    
	    Address destination = addressRepository.findById(dto.getDestinationAddressId())
	        .orElseThrow(() -> new RuntimeException("Destination address not found"));
	    
	    List<Address> stops = new ArrayList<>();
	    if (dto.getStopAddressIds() != null && !dto.getStopAddressIds().isEmpty()) {
	        stops = dto.getStopAddressIds().stream()
	            .map(id -> addressRepository.findById(id).orElseThrow())
	            .collect(java.util.stream.Collectors.toList());
	    }
	    
	    FavoriteRoute route = new FavoriteRoute();
	    route.setUser(user);
	    route.setName(dto.getName());
	    route.setPickupAddress(pickup);
	    route.setDestinationAddress(destination);
	    route.setStops(stops);
	    
	    FavoriteRoute saved = favoriteRouteRepository.save(route);
	    return mapFavoriteRouteToDTO(saved);
	}
	
	@Override
	public FavoriteRouteDTO updateFavoriteRoute(String routeId, FavoriteRouteDTO dto) {
	    FavoriteRoute route = favoriteRouteRepository.findById(routeId)
	        .orElseThrow(() -> new RuntimeException("Favorite route not found"));
	    
	    route.setName(dto.getName());
	    
	    if (dto.getPickupAddressId() != null) {
	        Address pickup = addressRepository.findById(dto.getPickupAddressId())
	            .orElseThrow(() -> new RuntimeException("Pickup address not found"));
	        route.setPickupAddress(pickup);
	    }
	    
	    if (dto.getDestinationAddressId() != null) {
	        Address destination = addressRepository.findById(dto.getDestinationAddressId())
	            .orElseThrow(() -> new RuntimeException("Destination address not found"));
	        route.setDestinationAddress(destination);
	    }
	    
	    if (dto.getStopAddressIds() != null) {
	        List<Address> stops = dto.getStopAddressIds().stream()
	            .map(id -> addressRepository.findById(id).orElseThrow())
	            .collect(java.util.stream.Collectors.toList());
	        route.setStops(stops);
	    }
	    
	    FavoriteRoute updated = favoriteRouteRepository.save(route);
	    return mapFavoriteRouteToDTO(updated);
	}
	
	@Override
	public void deleteFavoriteRoute(String routeId) {
	    favoriteRouteRepository.deleteById(routeId);
	}
	
	@Override
	public boolean hasActiveRide(String userId) {
        return rideRepository.existsByPassengers_IdAndStatusIn(
	        userId,
	        List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.IN_PROGRESS)
	    );
	}
	
	private FavoriteRouteDTO mapFavoriteRouteToDTO(FavoriteRoute route) {
	    FavoriteRouteDTO dto = new FavoriteRouteDTO();
	    dto.setId(route.getId());
	    dto.setName(route.getName());
	    dto.setUserId(route.getUser().getId());
	    dto.setPickupAddressId(route.getPickupAddress().getId());
	    dto.setDestinationAddressId(route.getDestinationAddress().getId());
	    
	    if (route.getStops() != null && !route.getStops().isEmpty()) {
	        List<String> stopIds = route.getStops().stream()
	            .map(Address::getId)
	            .collect(java.util.stream.Collectors.toList());
	        dto.setStopAddressIds(stopIds);
	    }
	    
	    return dto;
	}
	
	/**
	 * Calculate distance between two addresses using Haversine formula
	 * @param from Starting address
	 * @param to Destination address
	 * @return Distance in kilometers
	 */
	private double calculateDistance(Address from, Address to) {
	    final double EARTH_RADIUS_KM = 6371.0;
	    
	    double lat1 = Math.toRadians(from.getLatitude());
	    double lon1 = Math.toRadians(from.getLongitude());
	    double lat2 = Math.toRadians(to.getLatitude());
	    double lon2 = Math.toRadians(to.getLongitude());
	    
	    double dLat = lat2 - lat1;
	    double dLon = lon2 - lon1;
	    
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	               Math.cos(lat1) * Math.cos(lat2) *
	               Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    
	    return EARTH_RADIUS_KM * c;
	}

}

