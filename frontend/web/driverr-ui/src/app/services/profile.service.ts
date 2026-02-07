import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private api = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient) {}

  getProfile(userId: string) {
    return this.http.get<any>(`${this.api}/${userId}`);
  }

  updateProfile(userId: string, data: any) {
    return this.http.put<any>(`${this.api}/${userId}`, data);
  }

  // 2.3 – Change password
  changePassword(userId: string, passwordDto: any) {
    return this.http.post<any>(`${this.api}/${userId}/change-password`, passwordDto);
  }

  // 2.3 – Get profile change requests
  getProfileChangeRequests(userId: string) {
    return this.http.get<any[]>(`${this.api}/${userId}/profile-change-requests`);
  }

  // Admin - Get all pending profile change requests
  getAllPendingProfileChangeRequests() {
    return this.http.get<any[]>(`http://localhost:8081/api/admin/profile-change-requests`);
  }

  // Admin - Approve profile change request
  approveProfileChangeRequest(requestId: string) {
    return this.http.post<any>(`http://localhost:8081/api/admin/profile-change-requests/${requestId}/approve`, {});
  }

  // Admin - Reject profile change request
  rejectProfileChangeRequest(requestId: string) {
    return this.http.post<any>(`http://localhost:8081/api/admin/profile-change-requests/${requestId}/reject`, {});
  }
}
