package service.impl;

import dto.ride.*;

import java.util.List;
import java.util.UUID;

import domain.entities.Ride;
import domain.enums.RideStatus;
import service.RideService;

public class RideServiceImpl implements RideService {

    @Override
    public RideResponseDTO orderRide(RideOrderDTO dto) {

        RideResponseDTO response = new RideResponseDTO();
        response.setRideId("1L");                 // simulated ID
        response.setStatus(RideStatus.REQUESTED); // initial status
        response.setPrice(850.0);               // dummy price

        return response;
    }
    
    @Override
    public RideResponseDTO orderRideFromFavorite(
            String favoriteRouteId,
            FavoriteRideOrderDTO dto
    ) {
        RideResponseDTO response = new RideResponseDTO();

        response.setRideId(UUID.randomUUID().toString());
        response.setStatus(RideStatus.REQUESTED);
        response.setPrice(700.0); // "jeftinije jer je omiljena ruta"

        return response;
    }


	@Override
	public Ride createRide(Ride ride) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelRide(String rideId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Ride getRidebyId(String rideId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Ride> getActiveRides() {
		// TODO Auto-generated method stub
		return null;
	}
}

