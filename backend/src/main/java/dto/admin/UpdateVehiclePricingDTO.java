package dto.admin;

import jakarta.validation.constraints.PositiveOrZero;

public class UpdateVehiclePricingDTO {

    @PositiveOrZero(message = "Base price must be non-negative")
    private double basePrice;

    @PositiveOrZero(message = "Price per km must be non-negative")
    private double pricePerKm;

    public UpdateVehiclePricingDTO() {
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }
}
