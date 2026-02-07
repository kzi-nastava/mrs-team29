package controller;

import dto.map.GeocodeRequestDTO;
import dto.map.GeocodeResultDTO;
import dto.map.ReverseGeocodeRequestDTO;
import dto.map.RouteRequestDTO;
import dto.map.RouteResultDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.MapService;

@RestController
@RequestMapping("/api/map")
@CrossOrigin(origins = "http://localhost:4200")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @PostMapping("/geocode")
    public ResponseEntity<GeocodeResultDTO> geocode(@Valid @RequestBody GeocodeRequestDTO dto) {
        return ResponseEntity.ok(mapService.geocode(dto.getQuery()));
    }

    @PostMapping("/reverse")
    public ResponseEntity<GeocodeResultDTO> reverseGeocode(@Valid @RequestBody ReverseGeocodeRequestDTO dto) {
        return ResponseEntity.ok(mapService.reverseGeocode(dto.getLatitude(), dto.getLongitude()));
    }

    @PostMapping("/route")
    public ResponseEntity<RouteResultDTO> route(@Valid @RequestBody RouteRequestDTO dto) {
        return ResponseEntity.ok(
                mapService.getRoute(
                        dto.getFromLatitude(),
                        dto.getFromLongitude(),
                        dto.getToLatitude(),
                        dto.getToLongitude()
                )
        );
    }
}
