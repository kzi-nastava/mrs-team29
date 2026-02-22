import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserBlockStatus, BlockUserRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = 'http://localhost:8081/api/admin';

  constructor(private http: HttpClient) {}

  getAllUsersBlockStatus(): Observable<UserBlockStatus[]> {
    return this.http.get<UserBlockStatus[]>(`${this.apiUrl}/users/block-status`);
  }

  getUserBlockStatus(userId: string): Observable<UserBlockStatus> {
    return this.http.get<UserBlockStatus>(`${this.apiUrl}/users/${userId}/block-status`);
  }

  blockUser(userId: string, blockNote: string): Observable<any> {
    const request: BlockUserRequest = {
      userId,
      blocked: true,
      blockNote
    };
    return this.http.post(`${this.apiUrl}/users/${userId}/block`, request);
  }

  unblockUser(userId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/users/${userId}/unblock`, {});
  }

  // ============ PRICING MANAGEMENT (2.14) ============

  getAllPricing(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pricing`);
  }

  updatePricing(vehicleType: string, pricingData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/pricing/${vehicleType}`, pricingData);
  }
}
