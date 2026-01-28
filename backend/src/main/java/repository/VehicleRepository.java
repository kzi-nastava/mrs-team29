package repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.*;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    Optional<Vehicle> findByRegistrationPlate(String registrationPlate);
}

