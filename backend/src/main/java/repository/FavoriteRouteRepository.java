package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.*;

@Repository
public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, String> {

    List<FavoriteRoute> findByUser_Id(String userId);

    boolean existsByUser_IdAndPickupAddressAndDestinationAddress(
        String userId,
        Address pickupAddress,
        Address destinationAddress
    );
}

