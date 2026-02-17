package service.impl;

import dto.ride.RideOrderDTO;
import domain.entities.*;
import domain.enums.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.*;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {
    private RideRepository rideRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private FavoriteRouteRepository favoriteRouteRepository;

    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void orderRide_userNotFound_throws() {
        RideOrderDTO dto = buildOrderDto("user-1", "addr-1", "addr-2");
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rideService.orderRide(dto));
        assertEquals("User not found", ex.getMessage());
    }

     @Test
    void orderRide_activeRide_throws() {
        User user = buildUser("user-1", "user1@test.com");
        RideOrderDTO dto = buildOrderDto(user.getId(), "addr-1", "addr-2");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rideRepository.existsByPassengers_IdAndStatusIn(eq(user.getId()), any()))
            .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rideService.orderRide(dto));
        assertEquals("User already has active ride", ex.getMessage());
    }

    @Test
    void orderRide_scheduledTimeInPast_throws() {
        User user = buildUser("user-1", "user1@test.com");
        RideOrderDTO dto = buildOrderDto(user.getId(), "addr-1", "addr-2");
        dto.setScheduledTime(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rideRepository.existsByPassengers_IdAndStatusIn(eq(user.getId()), any()))
            .thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rideService.orderRide(dto));
        assertEquals("Cannot schedule rides in the past", ex.getMessage());
    }

    @Test
    void orderRide_noAvailableDrivers_throws() {
        User user = buildUser("user-1", "user1@test.com");
        Address pickup = buildAddress("addr-1");
        Address destination = buildAddress("addr-2");
        RideOrderDTO dto = buildOrderDto(user.getId(), pickup.getId(), destination.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rideRepository.existsByPassengers_IdAndStatusIn(eq(user.getId()), any()))
            .thenReturn(false);
        when(addressRepository.findById(pickup.getId())).thenReturn(Optional.of(pickup));
        when(addressRepository.findById(destination.getId())).thenReturn(Optional.of(destination));
        when(driverRepository.findFirstByStatusAndIsActiveTrueAndIsBlockedFalseOrderByIdAsc(DriverStatus.AVAILABLE))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rideService.orderRide(dto));
        assertEquals("No available drivers", ex.getMessage());
    }

    private RideOrderDTO buildOrderDto(String creatorId, String pickupId, String destinationId) {
        RideOrderDTO dto = new RideOrderDTO();
        dto.setCreatorId(creatorId);
        dto.setPickupAddressId(pickupId);
        dto.setDestinationAddressId(destinationId);
        return dto;
    }

    private User buildUser(String id, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName("test-user");
        user.setEmail(email);
        user.setPassword("pass");
        user.setUserType(UserType.CLIENT);
        user.setIsActive(true);
        user.setIsBlocked(false);
        return user;
    }

    private Address buildAddress(String id) {
        Address address = new Address();
        address.setId(id);
        address.setStreet("Test");
        address.setStreetNumber("1");
        address.setCity("Novi Sad");
        address.setCountry("Serbia");
        address.setLatitude(45.0);
        address.setLongitude(19.0);
        return address;
    }

    private Driver buildDriver(String id, String email) {
        Driver driver = new Driver();
        driver.setId(id);
        driver.setFirstName("Test");
        driver.setLastName("Driver");
        driver.setUserName("driver");
        driver.setEmail(email);
        driver.setPassword("pass");
        driver.setUserType(UserType.DRIVER);
        driver.setIsActive(true);
        driver.setIsBlocked(false);
        driver.setStatus(DriverStatus.AVAILABLE);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleModel("Model X");
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setRegistrationPlate("TEST-123");
        vehicle.setSeats(4);
        vehicle.setPetsAllowed(false);
        vehicle.setBabiesAllowed(false);
        driver.setVehicle(vehicle);

        return driver;
    }
}
