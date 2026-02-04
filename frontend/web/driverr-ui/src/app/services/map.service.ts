import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GeocodeResult {
  displayName: string;
  street: string;
  streetNumber: string;
  city: string;
  postalCode: string;
  country: string;
  latitude: number;
  longitude: number;
}

export interface AddressResponse {
  id: string;
  street: string;
  streetNumber: string;
  city: string;
  postalCode: string;
  country: string;
  latitude: number;
  longitude: number;
  displayName: string;
}

export interface RouteResult {
  distanceMeters: number;
  durationSeconds: number;
}

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  // Search for address and get geocode results
  geocode(query: string): Observable<GeocodeResult> {
    return this.http.post<GeocodeResult>(`${this.apiUrl}/map/geocode`, { query });
  }

  // Get address from coordinates
  reverseGeocode(latitude: number, longitude: number): Observable<GeocodeResult> {
    return this.http.post<GeocodeResult>(`${this.apiUrl}/map/reverse`, { latitude, longitude });
  }

  // Save address to database and get ID
  geocodeAndSave(query: string): Observable<AddressResponse> {
    return this.http.post<AddressResponse>(`${this.apiUrl}/addresses/geocode`, { query });
  }

  // Save address from coordinates to database and get ID
  reverseGeocodeAndSave(latitude: number, longitude: number): Observable<AddressResponse> {
    return this.http.post<AddressResponse>(`${this.apiUrl}/addresses/reverse`, { latitude, longitude });
  }

  // Get route information (distance and duration)
  getRoute(fromLat: number, fromLon: number, toLat: number, toLon: number): Observable<RouteResult> {
    return this.http.post<RouteResult>(`${this.apiUrl}/map/route`, {
      fromLatitude: fromLat,
      fromLongitude: fromLon,
      toLatitude: toLat,
      toLongitude: toLon
    });
  }

  // Get address by ID
  getAddressById(id: string): Observable<AddressResponse> {
    return this.http.get<AddressResponse>(`${this.apiUrl}/addresses/${id}`);
  }
}
