package domain.entities;

import java.time.LocalDateTime;

import domain.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ride_pricing")
public class RidePricing {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(name = "base_price", nullable = false)
    private double basePrice;

    @Column(name = "price_per_km", nullable = false)
    private double pricePerKm;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RidePricing() {
    }

    public RidePricing(VehicleType vehicleType, double basePrice, double pricePerKm) {
        this.vehicleType = vehicleType;
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
    }

    @PrePersist
    @PreUpdate
    public void onPersistOrUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
