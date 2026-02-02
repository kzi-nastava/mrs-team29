import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FavoriteRoute } from '../models/favorite-route.model';

@Injectable({ providedIn: 'root' })
export class FavoriteRouteService {

  private api = 'http://localhost:8081/api/favorite-routes';

  constructor(private http: HttpClient) {}

  // Get user's favorite routes
  getMyFavorites(userId: string): Observable<FavoriteRoute[]> {
    return this.http.get<FavoriteRoute[]>(`${this.api}/user/${userId}`);
  }

  // Create new favorite route
  createFavorite(route: any): Observable<FavoriteRoute> {
    return this.http.post<FavoriteRoute>(this.api, route);
  }

  // Update favorite route
  updateFavorite(routeId: string, route: any): Observable<FavoriteRoute> {
    return this.http.put<FavoriteRoute>(`${this.api}/${routeId}`, route);
  }

  // Delete favorite route
  deleteFavorite(routeId: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/${routeId}`);
  }

  // Order ride from favorite route
  orderFromFavorite(routeId: string, userId: string) {
    return this.http.post(
      `http://localhost:8081/api/rides/from-favorite/${routeId}`,
      { clientId: userId }
    );
  }
}


