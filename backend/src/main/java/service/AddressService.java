package service;

import dto.map.AddressResponseDTO;

public interface AddressService {

    AddressResponseDTO geocodeAndSave(String query);

    AddressResponseDTO reverseGeocodeAndSave(double latitude, double longitude);

    AddressResponseDTO getAddressById(String id);
}
