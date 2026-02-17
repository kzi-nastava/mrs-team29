package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import domain.entities.*;
import domain.enums.*;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@SuppressWarnings("null")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RideRepositoryTest {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AddressRepository addressRepository;

    private String userId;
    private String driverId;
    private String pickupId;
    private String destinationId;

    @BeforeEach
    void setUp() {
        User user = buildUser("user-" + UUID.randomUUID(), "user@test.com");
        user = userRepository.save(user);
        userId = user.getId();

        Address pickup = buildAddress("Pickup", "1");
        Address destination = buildAddress("Destination", "2");
        pickup = addressRepository.save(pickup);
        destination = addressRepository.save(destination);
        pickupId = pickup.getId();
        destinationId = destination.getId();

        Driver driver = buildDriver("driver-" + UUID.randomUUID(), "driver@test.com");
        driver = driverRepository.save(driver);
        driverId = driver.getId();

        Ride assigned = buildRide(RideStatus.ASSIGNED, LocalDateTime.now().minusMinutes(5));
        Ride finished = buildRide(RideStatus.FINISHED, LocalDateTime.now().minusMinutes(1));
        rideRepository.saveAll(List.of(assigned, finished));
    }

    @AfterEach
    void tearDown() {
        rideRepository.findByPassengers_IdAndStatus(userId, RideStatus.ASSIGNED)
            .forEach(ride -> rideRepository.deleteById(ride.getId()));
        rideRepository.findByPassengers_IdAndStatus(userId, RideStatus.FINISHED)
            .forEach(ride -> rideRepository.deleteById(ride.getId()));

        if (driverId != null) {
            driverRepository.deleteById(driverId);
        }
        if (pickupId != null) {
            addressRepository.deleteById(pickupId);
        }
        if (destinationId != null) {
            addressRepository.deleteById(destinationId);
        }
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    @Test
    void existsByPassengers_IdAndStatusIn_returnsTrueForActiveRide() {
        boolean exists = rideRepository.existsByPassengers_IdAndStatusIn(
            userId,
            List.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.IN_PROGRESS)
        );
        assertTrue(exists);
    }

    @Test
    void findFirstByDriver_IdAndStatusInOrderByTimestampsDesc_returnsLatestRide() {
        var result = rideRepository.findFirstByDriver_IdAndStatusInOrderByTimestampsDesc(
            driverId,
            List.of(RideStatus.ASSIGNED, RideStatus.FINISHED)
        );

        assertTrue(result.isPresent());
        assertEquals(RideStatus.FINISHED, result.get().getStatus());
    }

    @Test
    void findUserRideHistory_returnsOnlyFinishedRides() {
        var history = rideRepository.findUserRideHistory(userId, RideStatus.FINISHED);
        assertEquals(1, history.size());
        assertEquals(RideStatus.FINISHED, history.get(0).getStatus());
    }
    
    @Test
    void findByPassengers_IdAndStatus_returnsCorrectRides() {
        var assignedRides = rideRepository.findByPassengers_IdAndStatus(userId, RideStatus.ASSIGNED);
        assertEquals(1, assignedRides.size());
        assertEquals(RideStatus.ASSIGNED, assignedRides.get(0).getStatus());
        
        var finishedRides = rideRepository.findByPassengers_IdAndStatus(userId, RideStatus.FINISHED);
        assertEquals(1, finishedRides.size());
        assertEquals(RideStatus.FINISHED, finishedRides.get(0).getStatus());
    }

    private Ride buildRide(RideStatus status, LocalDateTime timestamp) {
        Ride ride = new Ride();
        ride.setPickupAddress(addressRepository.findById(pickupId).orElseThrow());
        ride.setDestinationAddress(addressRepository.findById(destinationId).orElseThrow());
        ride.setPassengers(List.of(userRepository.findById(userId).orElseThrow()));
        ride.setDriver(driverRepository.findById(driverId).orElseThrow());
        ride.setStatus(status);
        ride.setPrice(500);
        ride.setTimestamps(List.of(timestamp));
        return ride;
    }

    private User buildUser(String userName, String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName(userName);
        user.setEmail(email.replace("@", "+" + UUID.randomUUID() + "@"));
        user.setPassword("pass");
        user.setUserType(UserType.CLIENT);
        user.setIsActive(true);
        user.setIsBlocked(false);
        return user;
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
        driver.setStatus(DriverStatus.AVAILABLE);

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

    private Address buildAddress(String street, String number) {
        Address address = new Address();
        address.setStreet(street);
        address.setStreetNumber(number);
        address.setCity("Novi Sad");
        address.setCountry("Serbia");
        address.setPostalCode("21000");
        address.setLatitude(45.0);
        address.setLongitude(19.0);
        return address;
    }

}
