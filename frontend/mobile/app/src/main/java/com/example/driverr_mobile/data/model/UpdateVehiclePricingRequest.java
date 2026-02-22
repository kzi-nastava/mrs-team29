package com.example.driverr_mobile.data.model;

public class UpdateVehiclePricingRequest {

    private final double basePrice;
    private final double pricePerKm;

    public UpdateVehiclePricingRequest(double basePrice, double pricePerKm) {
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }
}
