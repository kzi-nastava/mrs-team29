package controller;

import dto.map.AddressResponseDTO;
import dto.map.GeocodeRequestDTO;
import dto.map.ReverseGeocodeRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AddressService;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:4200")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/geocode")
    public ResponseEntity<AddressResponseDTO> geocodeAndSave(@Valid @RequestBody GeocodeRequestDTO dto) {
        return ResponseEntity.ok(addressService.geocodeAndSave(dto.getQuery()));
    }

    @PostMapping("/reverse")
    public ResponseEntity<AddressResponseDTO> reverseGeocodeAndSave(@Valid @RequestBody ReverseGeocodeRequestDTO dto) {
        return ResponseEntity.ok(addressService.reverseGeocodeAndSave(dto.getLatitude(), dto.getLongitude()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddress(@PathVariable String id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }
}
