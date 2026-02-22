package com.example.driverr_mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class VehiclePricing {

    @SerializedName("vehicleType")
    private String vehicleType;

    @SerializedName("basePrice")
    private double basePrice;

    @SerializedName("pricePerKm")
    private double pricePerKm;

    @SerializedName("updatedAt")
    private String updatedAt;

    public String getVehicleType() {
        return vehicleType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getPricePerKm() {
        return pricePerKm;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
