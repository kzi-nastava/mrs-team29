package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import domain.entities.*;
import domain.enums.*;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {

    List<Driver> findByStatus(DriverStatus status);

    @Query("""
        SELECT d FROM Driver d
        WHERE d.status = :status
        AND d.isActive = true
        AND d.isBlocked = false
    """)
    List<Driver> findAvailableDrivers(@Param("status") DriverStatus status);

    @Query("""
        SELECT d FROM Driver d
        WHERE d.id = :driverId
        AND d.status = 'ACTIVE'
    """)
    Optional<Driver> findActiveDriverById(@Param("driverId") String driverId);
}
