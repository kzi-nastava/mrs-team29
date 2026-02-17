package controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ride.RideOrderDTO;
import domain.entities.*;
import domain.enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import repository.*;

@SpringBootTest
@AutoConfigureMockMvc
class RideControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RideRepository rideRepository;

    private String creatorId;
    private String pickupId;
    private String destinationId;
    private String driverId;

    @BeforeEach
    void setUp() {
        User creator = buildUser("creator-" + UUID.randomUUID(), "creator@test.com");
        creator = userRepository.save(creator);
        creatorId = creator.getId();

        Address pickup = buildAddress("Pickup", "1");
        Address destination = buildAddress("Destination", "2");
        pickup = addressRepository.save(pickup);
        destination = addressRepository.save(destination);
        pickupId = pickup.getId();
        destinationId = destination.getId();

        Driver driver = buildDriver("driver-" + UUID.randomUUID(), "driver@test.com");
        driver = driverRepository.save(driver);
        driverId = driver.getId();
    }

    private RideOrderDTO buildOrderDto() {
        RideOrderDTO dto = new RideOrderDTO();
        dto.setCreatorId(creatorId);
        dto.setPickupAddressId(pickupId);
        dto.setDestinationAddressId(destinationId);
        dto.setVehicleType(VehicleType.STANDARD);
        return dto;
    }

    private Ride buildRide(RideStatus status) {
        Ride ride = new Ride();
        ride.setPickupAddress(addressRepository.findById(pickupId).orElseThrow());
        ride.setDestinationAddress(addressRepository.findById(destinationId).orElseThrow());
        ride.setPassengers(List.of(userRepository.findById(creatorId).orElseThrow()));
        ride.setDriver(driverRepository.findById(driverId).orElseThrow());
        ride.setStatus(status);
        ride.setPrice(500);
        ride.setTimestamps(List.of(LocalDateTime.now()));
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
