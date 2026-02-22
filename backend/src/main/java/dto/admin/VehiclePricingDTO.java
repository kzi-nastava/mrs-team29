package dto.admin;

import java.time.LocalDateTime;

import domain.entities.RidePricing;
import domain.enums.VehicleType;

public class VehiclePricingDTO {

    private VehicleType vehicleType;
    private double basePrice;
    private double pricePerKm;
    private LocalDateTime updatedAt;

    public VehiclePricingDTO() {
    }

    public VehiclePricingDTO(VehicleType vehicleType, double basePrice, double pricePerKm, LocalDateTime updatedAt) {
        this.vehicleType = vehicleType;
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
        this.updatedAt = updatedAt;
    }

    public static VehiclePricingDTO fromEntity(RidePricing pricing) {
        return new VehiclePricingDTO(
            pricing.getVehicleType(),
            pricing.getBasePrice(),
            pricing.getPricePerKm(),
            pricing.getUpdatedAt()
        );
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
