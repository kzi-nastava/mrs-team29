package repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {
    Optional<Rating> findByRide_IdAndPassenger_Id(String rideId, String passengerId);
    boolean existsByRide_IdAndPassenger_Id(String rideId, String passengerId);
}
