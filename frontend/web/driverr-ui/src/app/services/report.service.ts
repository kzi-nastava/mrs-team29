import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideReportResponse } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private api = 'http://localhost:8081/api/reports';

  constructor(private http: HttpClient) {}

  getUserReport(userId: string, startDate?: string, endDate?: string): Observable<RideReportResponse> {
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }

    return this.http.get<RideReportResponse>(`${this.api}/user/${userId}`, { params });
  }

  getAdminReport(scope: 'DRIVER' | 'CLIENT', userId?: string, startDate?: string, endDate?: string): Observable<RideReportResponse> {
    let params = new HttpParams().set('scope', scope);
    if (userId) {
      params = params.set('userId', userId);
    }
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }

    return this.http.get<RideReportResponse>(`${this.api}/admin`, { params });
  }
}
