package service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import domain.entities.RidePricing;
import domain.enums.VehicleType;
import dto.admin.UpdateVehiclePricingDTO;
import dto.admin.VehiclePricingDTO;
import jakarta.annotation.PostConstruct;
import repository.RidePricingRepository;
import service.RidePricingService;

@Service
public class RidePricingServiceImpl implements RidePricingService {

    private final RidePricingRepository ridePricingRepository;

    private static final Map<VehicleType, double[]> DEFAULT_PRICING = new EnumMap<>(VehicleType.class);

    static {
        DEFAULT_PRICING.put(VehicleType.STANDARD, new double[] {300, 120});
        DEFAULT_PRICING.put(VehicleType.LUXURY, new double[] {500, 120});
        DEFAULT_PRICING.put(VehicleType.VAN, new double[] {400, 120});
    }

    public RidePricingServiceImpl(RidePricingRepository ridePricingRepository) {
        this.ridePricingRepository = ridePricingRepository;
    }

    @PostConstruct
    public void seedDefaultsIfMissing() {
        for (VehicleType type : VehicleType.values()) {
            if (ridePricingRepository.findById(type).isEmpty()) {
                double[] defaults = DEFAULT_PRICING.get(type);
                ridePricingRepository.save(new RidePricing(type, defaults[0], defaults[1]));
            }
        }
    }

    @Override
    public List<VehiclePricingDTO> getAllPricing() {
        seedDefaultsIfMissing();
        return ridePricingRepository.findAll().stream()
            .map(VehiclePricingDTO::fromEntity)
            .toList();
    }

    @Override
    public VehiclePricingDTO updatePricing(VehicleType vehicleType, UpdateVehiclePricingDTO dto) {
        if (dto.getBasePrice() < 0 || dto.getPricePerKm() < 0) {
            throw new RuntimeException("Pricing values must be non-negative");
        }

        RidePricing pricing = ridePricingRepository.findById(vehicleType)
            .orElseGet(() -> {
                double[] defaults = DEFAULT_PRICING.get(vehicleType);
                return new RidePricing(vehicleType, defaults[0], defaults[1]);
            });

        pricing.setBasePrice(dto.getBasePrice());
        pricing.setPricePerKm(dto.getPricePerKm());

        RidePricing saved = ridePricingRepository.save(pricing);
        return VehiclePricingDTO.fromEntity(saved);
    }

    @Override
    public double calculateRidePrice(VehicleType vehicleType, double distanceKm) {
        VehicleType type = vehicleType == null ? VehicleType.STANDARD : vehicleType;

        RidePricing pricing = ridePricingRepository.findById(type)
            .orElseGet(() -> {
                double[] defaults = DEFAULT_PRICING.get(type);
                return ridePricingRepository.save(new RidePricing(type, defaults[0], defaults[1]));
            });

        return pricing.getBasePrice() + (Math.max(0, distanceKm) * pricing.getPricePerKm());
    }
}
