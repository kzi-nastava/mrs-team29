import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class DriverService {

  private api = 'http://localhost:5050/api/drivers';

  constructor(private http: HttpClient) {}

  registerDriver(data: any) {
    return this.http.post(`${this.api}/register`, data);
  }
}
