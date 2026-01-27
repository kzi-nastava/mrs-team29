import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private api = 'http://localhost:5050/api/users';

  constructor(private http: HttpClient) {}

  getProfile(userId: string) {
    return this.http.get<any>(`${this.api}/${userId}`);
  }

  updateProfile(userId: string, data: any) {
    return this.http.put<any>(`${this.api}/${userId}`, data);
  }
}
