package service;

import java.util.List;

import domain.entities.Ride;

public interface RideService {
	Ride createRide(Ride ride);
	void cancelRide(String rideId);
	Ride getRidebyId(String rideId);
	List<Ride> getActiveRides();
}
