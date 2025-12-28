package service;

import java.util.List;

import domain.entities.Ride;
import dto.ride.*;

public interface RideService {
	Ride createRide(Ride ride);
	void cancelRide(String rideId);
	Ride getRidebyId(String rideId);
	List<Ride> getActiveRides();
	RideResponseDTO orderRide(RideOrderDTO dto);
	RideResponseDTO orderRideFromFavorite(String favoriteRouteId, FavoriteRideOrderDTO dto);
}
