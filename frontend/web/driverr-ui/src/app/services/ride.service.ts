import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ride } from '../models/ride.model';

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

  // Get user's ride history
  getUserRideHistory(userId: string): Observable<Ride[]> {
    return this.http.get<Ride[]>(`${this.api}/user/${userId}/history`);
  }

  // ============ DRIVER ENDPOINTS ============
  
  // Get driver's current assigned/in-progress ride
  getDriverCurrentRide(driverId: string): Observable<Ride> {
    return this.http.get<Ride>(`${this.api}/driver/${driverId}/current`);
  }

  // Start a ride (driver begins journey with passengers)
  startRide(rideId: string, driverId: string): Observable<Ride> {
    return this.http.post<Ride>(`${this.api}/${rideId}/start?driverId=${driverId}`, {});
  }

  // Finish a ride (driver completes journey)
  finishRide(rideId: string, driverId: string): Observable<Ride> {
    return this.http.post<Ride>(`${this.api}/${rideId}/finish?driverId=${driverId}`, {});
  }

  // Get driver's ride history
  getDriverRideHistory(driverId: string): Observable<Ride[]> {
    return this.http.get<Ride[]>(`${this.api}/driver/${driverId}/history`);
  }

  // ============ FAVORITE ROUTES ============

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

