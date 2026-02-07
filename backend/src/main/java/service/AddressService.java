package service;

import dto.map.AddressResponseDTO;
import dto.map.AddressSaveRequestDTO;

public interface AddressService {

    AddressResponseDTO geocodeAndSave(String query);

    AddressResponseDTO reverseGeocodeAndSave(double latitude, double longitude);

    AddressResponseDTO saveFromGeocode(AddressSaveRequestDTO dto);

    AddressResponseDTO getAddressById(String id);
}
