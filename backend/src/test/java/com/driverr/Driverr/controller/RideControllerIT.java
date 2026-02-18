package com.driverr.Driverr.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ride.RideOrderDTO;
import domain.entities.*;
import domain.enums.*;
import main.DriverrApplication;
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

@SpringBootTest(classes = main.DriverrApplication.class)
@AutoConfigureMockMvc
@SuppressWarnings("null")
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

     @AfterEach
    void tearDown() {
        List<RideStatus> statuses = List.of(
            RideStatus.REQUESTED,
            RideStatus.ASSIGNED,
            RideStatus.IN_PROGRESS,
            RideStatus.FINISHED
        );
        for (RideStatus status : statuses) {
            rideRepository.findByPassengers_IdAndStatus(creatorId, status)
                .forEach(ride -> rideRepository.deleteById(ride.getId()));
        }
        if (driverId != null) {
            driverRepository.deleteById(driverId);
        }
        if (pickupId != null) {
            addressRepository.deleteById(pickupId);
        }
        if (destinationId != null) {
            addressRepository.deleteById(destinationId);
        }
        if (creatorId != null) {
            userRepository.deleteById(creatorId);
        }
    }

    @Test
    void orderRide_success_returnsCreated() throws Exception {
        RideOrderDTO dto = buildOrderDto();

        mockMvc.perform(post("/api/rides")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
        
        // Verify a ride was created
        List<Ride> createdRides = rideRepository.findByPassengers_IdAndStatus(creatorId, RideStatus.ASSIGNED);
        assertTrue(createdRides.size() > 0, "Ride should be created");
    }

    @Test
    void orderRide_activeRide_returnsBadRequest() throws Exception {
        Ride existing = buildRide(RideStatus.ASSIGNED);
        rideRepository.save(existing);

        RideOrderDTO dto = buildOrderDto();

        mockMvc.perform(post("/api/rides")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void orderRide_scheduledTimeInPast_returnsBadRequest() throws Exception {
        RideOrderDTO dto = buildOrderDto();
        dto.setScheduledTime(LocalDateTime.now().minusMinutes(10));

        mockMvc.perform(post("/api/rides")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
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
