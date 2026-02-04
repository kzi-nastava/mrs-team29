package service;

import dto.map.GeocodeResultDTO;
import dto.map.RouteResultDTO;

public interface MapService {

    GeocodeResultDTO geocode(String query);

    GeocodeResultDTO reverseGeocode(double latitude, double longitude);

    RouteResultDTO getRoute(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude);
}
