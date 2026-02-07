package controller;

import dto.ride.FavoriteRouteDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RideService;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-routes")
@CrossOrigin(origins = "http://localhost:4200")
public class FavoriteRouteController {

    private final RideService rideService;

    public FavoriteRouteController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteRouteDTO>> getUserFavoriteRoutes(@PathVariable String userId) {
        List<FavoriteRouteDTO> routes = rideService.getUserFavoriteRoutes(userId);
        return ResponseEntity.ok(routes);
    }

    @PostMapping
    public ResponseEntity<FavoriteRouteDTO> createFavoriteRoute(@Valid @RequestBody FavoriteRouteDTO dto) {
        FavoriteRouteDTO created = rideService.createFavoriteRoute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<FavoriteRouteDTO> updateFavoriteRoute(
            @PathVariable String routeId,
            @Valid @RequestBody FavoriteRouteDTO dto) {
        FavoriteRouteDTO updated = rideService.updateFavoriteRoute(routeId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteFavoriteRoute(@PathVariable String routeId) {
        rideService.deleteFavoriteRoute(routeId);
        return ResponseEntity.ok("Favorite route deleted");
    }
}
