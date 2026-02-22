package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.entities.RidePricing;
import domain.enums.VehicleType;

@Repository
public interface RidePricingRepository extends JpaRepository<RidePricing, VehicleType> {
}
