import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class RideService {

  private api = 'http://localhost:8081/api/rides';

  constructor(private http: HttpClient) {}

  // 2.4.1 – Order Ride (manual route with passengers, scheduling, vehicle type)
  orderRide(dto: any) {
    return this.http.post<any>(this.api, dto);
  }

  // 2.6.1 – Check if user has active ride
  hasActiveRide(userId: string) {
    return this.http.get<boolean>(`${this.api}/user/${userId}/active`);
  }

  // Get user's rides
  getUserRides(userId: string) {
    return this.http.get<any[]>(`${this.api}/user/${userId}`);
  }

  // Start ride (driver action)
  startRide(rideId: string) {
    return this.http.patch(`${this.api}/${rideId}/start`, {});
  }

  // 2.4.3 – Favorite Routes
  getFavoriteRoutes(userId: string) {
    return this.http.get<any[]>(`${this.api}/favorite-routes/user/${userId}`);
  }

  createFavoriteRoute(dto: any) {
    return this.http.post<any>(`${this.api}/favorite-routes`, dto);
  }

  updateFavoriteRoute(routeId: string, dto: any) {
    return this.http.put<any>(`${this.api}/favorite-routes/${routeId}`, dto);
  }

  deleteFavoriteRoute(routeId: string) {
    return this.http.delete(`${this.api}/favorite-routes/${routeId}`);
  }

  // Order Ride from Favorite Route
  orderRideFromFavorite(favoriteRouteId: string, clientId: string) {
    return this.http.post<any>(
      `${this.api}/favorites/${favoriteRouteId}`,
      { clientId }
    );
  }
}

