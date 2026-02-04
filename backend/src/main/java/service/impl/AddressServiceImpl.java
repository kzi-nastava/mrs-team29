package service.impl;

import dto.map.AddressResponseDTO;
import dto.map.GeocodeResultDTO;
import domain.entities.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AddressRepository;
import service.AddressService;
import service.MapService;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final MapService mapService;

    public AddressServiceImpl(AddressRepository addressRepository, MapService mapService) {
        this.addressRepository = addressRepository;
        this.mapService = mapService;
    }

    @Override
    @Transactional
    public AddressResponseDTO geocodeAndSave(String query) {
        GeocodeResultDTO geocode = mapService.geocode(query);
        Address address = findOrCreate(geocode);
        return AddressResponseDTO.from(address, geocode.getDisplayName());
    }

    @Override
    @Transactional
    public AddressResponseDTO reverseGeocodeAndSave(double latitude, double longitude) {
        GeocodeResultDTO geocode = mapService.reverseGeocode(latitude, longitude);
        Address address = findOrCreate(geocode);
        return AddressResponseDTO.from(address, geocode.getDisplayName());
    }

    @Override
    public AddressResponseDTO getAddressById(String id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return AddressResponseDTO.from(address, null);
    }

    private Address findOrCreate(GeocodeResultDTO geocode) {
        return addressRepository.findByLatitudeAndLongitude(geocode.getLatitude(), geocode.getLongitude())
                .orElseGet(() -> {
                    Address address = new Address();
                    address.setStreet(geocode.getStreet());
                    address.setStreetNumber(geocode.getStreetNumber());
                    address.setCity(geocode.getCity());
                    address.setPostalCode(geocode.getPostalCode());
                    address.setCountry(geocode.getCountry());
                    address.setLatitude(geocode.getLatitude());
                    address.setLongitude(geocode.getLongitude());
                    return addressRepository.save(address);
                });
    }
}
