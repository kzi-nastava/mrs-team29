package service.impl;

import dto.ride.*;
import dto.admin.AdminRideStateDTO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import domain.entities.*;
import domain.enums.*;
import service.EmailService;
import service.NotificationService;
import service.RidePricingService;
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
    private final RidePricingService ridePricingService;
    private final DriverInconsistencyNoteRepository inconsistencyNoteRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
	
    public RideServiceImpl(
            RideRepository rideRepository,
            UserRepository userRepository,
            DriverRepository driverRepository,
            AddressRepository addressRepository,
            FavoriteRouteRepository favoriteRouteRepository,
            RidePricingService ridePricingService,
            DriverInconsistencyNoteRepository inconsistencyNoteRepository,
                EmailService emailService,
                NotificationService notificationService
    ) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.addressRepository = addressRepository;
        this.favoriteRouteRepository = favoriteRouteRepository;
        this.ridePricingService = ridePricingService;
        this.inconsistencyNoteRepository = inconsistencyNoteRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }
    	
    @Override
    public RideResponseDTO orderRide(RideOrderDTO dto) {

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is blocked
        if (creator.getIsBlocked()) {
            String message = "Your account has been blocked";
            if (creator.getBlockNote() != null && !creator.getBlockNote().isBlank()) {
                message += ": " + creator.getBlockNote();
            }
            throw new RuntimeException(message);
        }

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
        
        double distanceKm = calculateDistance(pickup, destination);
        ride.setPrice(ridePricingService.calculateRidePrice(dto.getVehicleType(), distanceKm));
        
        ride.setTimestamps(List.of(nowInAppZone()));

        rideRepository.save(ride);

        notifyRideAccepted(ride, creator);

        return RideResponseDTO.fromRide(ride);
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

        if (ride.getStatus() != RideStatus.ASSIGNED && ride.getStatus() != RideStatus.SCHEDULED) {
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

        // Send email notifications to all passengers
        for (User passenger : ride.getPassengers()) {
            try {
                String pickupAddr = ride.getPickupAddress().getStreet() + " " + 
                                    ride.getPickupAddress().getStreetNumber() + ", " + 
                                    ride.getPickupAddress().getCity();
                String destAddr = ride.getDestinationAddress().getStreet() + " " + 
                                  ride.getDestinationAddress().getStreetNumber() + ", " + 
                                  ride.getDestinationAddress().getCity();
                
                emailService.sendRideFinishedEmail(
                    passenger.getEmail(),
                    passenger.getFirstName() + " " + passenger.getLastName(),
                    ride.getId(),
                    pickupAddr,
                    destAddr,
                    ride.getPrice()
                );

                notificationService.createNotification(
                    passenger,
                    ride,
                    NotificationType.RIDE_FINISHED,
                    "Ride completed",
                    "Your ride has been completed. You can rate the driver and vehicle now."
                );
            } catch (Exception e) {
                System.err.println("Failed to send ride finished email to " + passenger.getEmail() + ": " + e.getMessage());
            }
        }

        return RideResponseDTO.fromRide(ride);
    }
    
    @Override
    public RideResponseDTO orderRideFromFavorite(String favoriteRouteId, FavoriteRideOrderDTO dto) {

        FavoriteRoute route = favoriteRouteRepository.findById(favoriteRouteId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        User creator = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is blocked
        if (creator.getIsBlocked()) {
            String message = "Your account has been blocked";
            if (creator.getBlockNote() != null && !creator.getBlockNote().isBlank()) {
                message += ": " + creator.getBlockNote();
            }
            throw new RuntimeException(message);
        }

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
        VehicleType vehicleType = (driver.getVehicle() != null && driver.getVehicle().getType() != null)
            ? driver.getVehicle().getType()
            : VehicleType.STANDARD;
        double distanceKm = calculateDistance(route.getPickupAddress(), route.getDestinationAddress());
        ride.setPrice(ridePricingService.calculateRidePrice(vehicleType, distanceKm));
        ride.setTimestamps(List.of(nowInAppZone()));

        rideRepository.save(ride);

        return RideResponseDTO.fromRide(ride);
    }

    @Override
    public RideResponseDTO getDriverCurrentRide(String driverId) {
        // First check for active rides (IN_PROGRESS, ASSIGNED, REQUESTED)
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

        // If no active rides, check for upcoming scheduled rides
        if (visibleRides.isEmpty()) {
            List<Ride> scheduledRides = rideRepository.findByDriver_IdAndStatusIn(
                driverId,
                List.of(RideStatus.SCHEDULED)
            );

            // Show scheduled rides that are within the next 2 hours
            visibleRides = scheduledRides.stream()
                .filter(ride -> ride.getScheduledTime() != null 
                    && ride.getScheduledTime().isAfter(now)
                    && ride.getScheduledTime().isBefore(now.plusHours(2)))
                .collect(Collectors.toList());
        }

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
    public RideResponseDTO getUserCurrentRide(String userId) {
        List<Ride> activeRides = rideRepository.findByPassengers_IdAndStatusIn(
            userId,
            List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.IN_PROGRESS)
        );

        if (activeRides.isEmpty()) {
            throw new RuntimeException("No active ride found for user");
        }

        // Return the most recent active ride (most likely there should only be one)
        Ride currentRide = activeRides.stream()
            .max(Comparator.comparing(
                Ride::getScheduledTime,
                Comparator.nullsLast(Comparator.naturalOrder())
            ))
            .orElse(activeRides.get(0));

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

    private void notifyRideAccepted(Ride ride, User creator) {
        if (ride.getPassengers() == null || ride.getPassengers().isEmpty()) {
            return;
        }

        String pickupAddr = ride.getPickupAddress() == null ? "" :
                ride.getPickupAddress().getStreet() + " " + ride.getPickupAddress().getStreetNumber() + ", " + ride.getPickupAddress().getCity();
        String destAddr = ride.getDestinationAddress() == null ? "" :
                ride.getDestinationAddress().getStreet() + " " + ride.getDestinationAddress().getStreetNumber() + ", " + ride.getDestinationAddress().getCity();

        for (User passenger : ride.getPassengers()) {
            if (creator != null && creator.getId() != null && creator.getId().equals(passenger.getId())) {
                continue;
            }

            try {
                String fullName = passenger.getFirstName() + " " + passenger.getLastName();
                emailService.sendRideAcceptedEmail(
                    passenger.getEmail(),
                    fullName,
                    ride.getId(),
                    pickupAddr,
                    destAddr
                );

                notificationService.createNotification(
                    passenger,
                    ride,
                    NotificationType.RIDE_ACCEPTED,
                    "Ride accepted",
                    "You have been added to a ride that has been accepted by a driver. Tap to track it."
                );
            } catch (Exception e) {
                System.err.println("Failed to send ride accepted notification to " + passenger.getEmail() + ": " + e.getMessage());
            }
        }
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

	@Override
	public InconsistencyNoteResponseDTO reportDriverInconsistency(InconsistencyNoteDTO dto, String passengerId) {
	    Ride ride = rideRepository.findById(dto.getRideId())
	            .orElseThrow(() -> new RuntimeException("Ride not found"));
	    
	    User passenger = userRepository.findById(passengerId)
	            .orElseThrow(() -> new RuntimeException("Passenger not found"));
	    
	    // Verify passenger is part of this ride
	    boolean isPassenger = ride.getPassengers().stream()
	            .anyMatch(p -> p.getId().equals(passengerId));
	    
	    if (!isPassenger) {
	        throw new RuntimeException("You are not a passenger on this ride");
	    }
	    
	    // Verify ride is in progress
	    if (ride.getStatus() != RideStatus.IN_PROGRESS) {
	        throw new RuntimeException("Can only report inconsistencies during active rides");
	    }
	    
	    DriverInconsistencyNote note = new DriverInconsistencyNote(ride, passenger, dto.getNoteText());
	    note = inconsistencyNoteRepository.save(note);
	    
	    return new InconsistencyNoteResponseDTO(
	            note.getId(),
	            ride.getId(),
	            passenger.getId(),
	            passenger.getFirstName() + " " + passenger.getLastName(),
	            note.getNoteText(),
	            note.getTimestamp()
	    );
	}

	@Override
	public List<InconsistencyNoteResponseDTO> getRideInconsistencyNotes(String rideId) {
	    List<DriverInconsistencyNote> notes = inconsistencyNoteRepository.findByRideId(rideId);
	    
	    return notes.stream()
	            .map(note -> new InconsistencyNoteResponseDTO(
	                    note.getId(),
	                    note.getRide().getId(),
	                    note.getPassenger().getId(),
	                    note.getPassenger().getFirstName() + " " + note.getPassenger().getLastName(),
	                    note.getNoteText(),
	                    note.getTimestamp()
	            ))
	            .collect(Collectors.toList());
	}

    @Override
    public List<AdminRideStateDTO> getAdminActiveRideStates(String driverName) {
        List<Ride> activeRides = rideRepository.findByStatusIn(
                List.of(RideStatus.ASSIGNED, RideStatus.IN_PROGRESS)
        );

        String normalizedDriverName = driverName == null ? "" : driverName.trim().toLowerCase(Locale.ROOT);

        return activeRides.stream()
                .filter(ride -> ride.getDriver() != null)
                .filter(ride -> {
                    if (normalizedDriverName.isBlank()) {
                        return true;
                    }
                    String firstName = ride.getDriver().getFirstName() == null ? "" : ride.getDriver().getFirstName();
                    String lastName = ride.getDriver().getLastName() == null ? "" : ride.getDriver().getLastName();
                    String fullName = (firstName + " " + lastName).trim().toLowerCase(Locale.ROOT);
                    return fullName.contains(normalizedDriverName)
                            || firstName.toLowerCase(Locale.ROOT).contains(normalizedDriverName)
                            || lastName.toLowerCase(Locale.ROOT).contains(normalizedDriverName);
                })
                .map(this::mapToAdminRideState)
                .collect(Collectors.toList());
    }

    private AdminRideStateDTO mapToAdminRideState(Ride ride) {
        AdminRideStateDTO dto = new AdminRideStateDTO();
        dto.setRideId(ride.getId());
        dto.setStatus(ride.getStatus());

        Driver driver = ride.getDriver();
        if (driver != null) {
            dto.setDriverId(driver.getId());
            dto.setDriverName((driver.getFirstName() + " " + driver.getLastName()).trim());
        }

        if (ride.getPickupAddress() != null) {
            dto.setPickupAddress(formatAddress(ride.getPickupAddress()));
        }
        if (ride.getDestinationAddress() != null) {
            dto.setDestinationAddress(formatAddress(ride.getDestinationAddress()));
        }

        dto.setScheduledTime(ride.getScheduledTime());
        dto.setStartedAt(getRideStartedAt(ride));
        if (dto.getStartedAt() != null) {
            dto.setEstimatedArrival(dto.getStartedAt().plusMinutes(15));
        }

        if (ride.getStatus() == RideStatus.IN_PROGRESS && ride.getPickupAddress() != null && ride.getDestinationAddress() != null) {
            double midLat = (ride.getPickupAddress().getLatitude() + ride.getDestinationAddress().getLatitude()) / 2.0;
            double midLon = (ride.getPickupAddress().getLongitude() + ride.getDestinationAddress().getLongitude()) / 2.0;
            dto.setCurrentLatitude(midLat);
            dto.setCurrentLongitude(midLon);
            dto.setCurrentLocationDescription("Estimated position between pickup and destination");
        } else if (ride.getPickupAddress() != null) {
            dto.setCurrentLatitude(ride.getPickupAddress().getLatitude());
            dto.setCurrentLongitude(ride.getPickupAddress().getLongitude());
            dto.setCurrentLocationDescription("Pickup location");
        }

        return dto;
    }

    private LocalDateTime getRideStartedAt(Ride ride) {
        if (ride.getTimestamps() == null || ride.getTimestamps().size() < 2) {
            return null;
        }
        return ride.getTimestamps().get(1);
    }

    private String formatAddress(Address address) {
        return address.getStreet() + " " + address.getStreetNumber() + ", " + address.getCity();
    }

}

