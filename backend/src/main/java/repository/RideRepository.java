package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.entities.*;
import domain.enums.*;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {
    List<Ride> findByStatus(RideStatus status);
    List<Ride> findByDriver_IdAndStatus(String driverId, RideStatus status);
    List<Ride> findByPassengers_IdAndStatus(String passengerId, RideStatus status);
    boolean existsByPassengers_IdAndStatusIn(String passengerId, List<RideStatus> statuses);
    
    // Find driver's current active ride
    Optional<Ride> findFirstByDriver_IdAndStatusInOrderByTimestampsDesc(String driverId, List<RideStatus> statuses);
    
    // Find driver's ride history (finished rides)
    List<Ride> findByDriver_IdAndStatusOrderByTimestampsDesc(String driverId, RideStatus status);
    
    // Find driver's ride history with date range (using finished_at from timestamps)
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId AND r.status = :status " +
           "AND SIZE(r.timestamps) >= 3 " +
           "AND (:startDate IS NULL OR r.timestamps[2] >= :startDate) " +
           "AND (:endDate IS NULL OR r.timestamps[2] <= :endDate) " +
           "ORDER BY r.timestamps DESC")
    List<Ride> findDriverRideHistoryByDateRange(String driverId, RideStatus status, 
                                                 java.time.LocalDateTime startDate, 
                                                 java.time.LocalDateTime endDate);
    
    // Find user's ride history
    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :userId AND r.status = :status ORDER BY r.scheduledTime DESC")
    List<Ride> findUserRideHistory(String userId, RideStatus status);
    
    // Check if driver has active ride
    boolean existsByDriver_IdAndStatusIn(String driverId, List<RideStatus> statuses);
}

