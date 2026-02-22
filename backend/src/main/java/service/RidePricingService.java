package service;

import java.util.List;

import domain.enums.VehicleType;
import dto.admin.UpdateVehiclePricingDTO;
import dto.admin.VehiclePricingDTO;

public interface RidePricingService {
    List<VehiclePricingDTO> getAllPricing();
    VehiclePricingDTO updatePricing(VehicleType vehicleType, UpdateVehiclePricingDTO dto);
    double calculateRidePrice(VehicleType vehicleType, double distanceKm);
}
