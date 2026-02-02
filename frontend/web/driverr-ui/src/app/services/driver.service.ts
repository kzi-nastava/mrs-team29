import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class DriverService {

  private api = 'http://localhost:8081/api/drivers';

  constructor(private http: HttpClient) {}

  registerDriver(data: any) {
    return this.http.post(`${this.api}/register`, data);
  }

  // 2.2.3 – Activate driver account with token
  activateDriver(activationDto: any) {
    return this.http.post(`${this.api}/activate`, activationDto);
  }

  // 2.3 – Get driver working hours in last 24 hours
  getWorkingHours(driverId: string) {
    return this.http.get<any>(`${this.api}/${driverId}/working-hours`);
  }

  getDriver(driverId: string) {
    return this.http.get<any>(`${this.api}/${driverId}`);
  }
}
