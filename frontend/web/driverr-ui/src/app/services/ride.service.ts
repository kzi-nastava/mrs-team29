import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class RideService {

  private api = 'http://localhost:5050/api/rides';

  constructor(private http: HttpClient) {}

  // 2.4.1 – Order Ride (manual route)
  orderRide(dto: any) {
    return this.http.post<any>(this.api, dto);
  }

  // Start ride (driver action)
  startRide(rideId: string) {
    return this.http.patch(`${this.api}/${rideId}/start`, {});
  }

  // 2.4.3 – Order Ride from Favorite Route
  orderRideFromFavorite(favoriteRouteId: string, clientId: string) {
    return this.http.post<any>(
      `${this.api}/favorite/${favoriteRouteId}`,
      { clientId }
    );
  }
}

