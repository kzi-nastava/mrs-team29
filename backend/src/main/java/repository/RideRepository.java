package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.*;
import domain.enums.*;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {

    List<Ride> findByStatus(RideStatus status);
    List<Ride> findByDriverIdAndStatus(String driverId, RideStatus status);
    List<Ride> findByPassengers_IdAndStatus(String passengerId, RideStatus status);
    boolean existsByPassengerIdAndStatusIn(String passengerId, List<RideStatus> statuses);
    Optional<Ride> findFirstByDriverIdAndStatusOrderByIdAsc(String driverId, RideStatus status);
	
}

