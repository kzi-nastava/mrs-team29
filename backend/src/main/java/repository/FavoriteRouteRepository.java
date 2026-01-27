package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.*;

@Repository
public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, String> {

    List<FavoriteRoute> findByUserId(String userId);

    boolean existsByUserIdAndPickupAddressAndDestinationAddress(
        String userId,
        Address pickupAddress,
        Address destinationAddress
    );
}

