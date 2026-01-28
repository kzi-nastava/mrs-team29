import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FavoriteRoute } from '../models/favorite-route.model';

@Injectable({ providedIn: 'root' })
export class FavoriteRouteService {

  private api = 'http://localhost:8080/api/favorites';

  constructor(private http: HttpClient) {}

  getMyFavorites(userId: string): Observable<FavoriteRoute[]> {
    return this.http.get<FavoriteRoute[]>(`${this.api}/user/${userId}`);
  }

  orderFromFavorite(routeId: string) {
    return this.http.post(
      `http://localhost:8080/api/rides/favorite/${routeId}`,
      { clientId: 'userId' }
    );
  }
}

