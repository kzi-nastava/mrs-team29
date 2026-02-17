package service.impl;

import dto.ride.RideOrderDTO;
import domain.entities.*;
import domain.enums.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private RideOrderDTO buildOrderDto(String creatorId, String pickupId, String destinationId) {
        RideOrderDTO dto = new RideOrderDTO();
        dto.setCreatorId(creatorId);
        dto.setPickupAddressId(pickupId);
        dto.setDestinationAddressId(destinationId);
        return dto;
    }

}
