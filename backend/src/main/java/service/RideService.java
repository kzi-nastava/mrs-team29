package service;

import java.util.List;

import domain.entities.Ride;
import dto.admin.AdminRideStateDTO;
import dto.ride.*;

public interface RideService {
	// Client side - order rides
	RideResponseDTO orderRide(RideOrderDTO dto);
	RideResponseDTO orderRideFromFavorite(String favoriteRouteId, FavoriteRideOrderDTO dto);
	boolean hasActiveRide(String userId);
	RideResponseDTO getUserCurrentRide(String userId);
	
	// Driver side - manage current ride
	RideResponseDTO getDriverCurrentRide(String driverId);
	RideResponseDTO startRide(String rideId, String driverId);
	RideResponseDTO finishRide(String rideId, String driverId);
	
	// Ride history
	List<RideResponseDTO> getDriverRideHistory(String driverId);
	List<RideResponseDTO> getDriverRideHistory(String driverId, java.time.LocalDate startDate, java.time.LocalDate endDate);
	List<RideResponseDTO> getUserRideHistory(String userId);
	
	// Favorite routes management
	List<FavoriteRouteDTO> getUserFavoriteRoutes(String userId);
	FavoriteRouteDTO createFavoriteRoute(FavoriteRouteDTO dto);
	FavoriteRouteDTO updateFavoriteRoute(String routeId, FavoriteRouteDTO dto);
	void deleteFavoriteRoute(String routeId);
	
	// Legacy/stub methods (to be implemented or removed)
	Ride createRide(Ride ride);
	void cancelRide(String rideId);
	Ride getRidebyId(String rideId);
	List<Ride> getActiveRides();
	
	// Inconsistency notes (2.6.2)
	InconsistencyNoteResponseDTO reportDriverInconsistency(InconsistencyNoteDTO dto, String passengerId);
	List<InconsistencyNoteResponseDTO> getRideInconsistencyNotes(String rideId);

	// Admin monitoring (2.13)
	List<AdminRideStateDTO> getAdminActiveRideStates(String driverName);
}
