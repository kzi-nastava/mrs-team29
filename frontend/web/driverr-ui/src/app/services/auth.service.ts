import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  token: string;
  isDriver: boolean;
  driverId?: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  profilePictureUrl?: string;
}

export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is already logged in (from localStorage)
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('currentUser');
    if (token && user) {
      this.isAuthenticatedSubject.next(true);
      this.currentUserSubject.next(JSON.parse(user));
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    console.log('[AuthService] login request', credentials);
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          console.log('[AuthService] login response', response);
          if (response.data) {
            localStorage.setItem('authToken', response.data.token);
            localStorage.setItem('userId', response.data.userId);
            localStorage.setItem('currentUser', JSON.stringify(response.data));
            this.isAuthenticatedSubject.next(true);
            this.currentUserSubject.next(response.data);
          }
        }),
        map(response => response.data)
      );
  }

  register(data: RegisterRequest): Observable<any> {
    console.log('[AuthService] register request', data);
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/register`, data)
      .pipe(
        tap(response => console.log('[AuthService] register response', response)),
        map(response => response)
      );
  }

  activateAccount(token: string): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/activate`, {
      params: { token }
    }).pipe(
      map(response => response)
    );
  }

  requestPasswordReset(email: string): Observable<any> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/password-reset/request`, { email })
      .pipe(
        map(response => response)
      );
  }

  resetPassword(token: string, newPassword: string, confirmPassword: string): Observable<any> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/password-reset/reset`, {
      token,
      newPassword,
      confirmPassword
    }).pipe(
      map(response => response)
    );
  }

  logout(): Observable<any> {
    const userId = this.getUserId();
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/logout`, null, {
      params: { userId }
    }).pipe(
      tap(() => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('currentUser');
        this.isAuthenticatedSubject.next(false);
        this.currentUserSubject.next(null);
      }),
      map(response => response)
    );
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }

  getUserId(): string {
    return localStorage.getItem('userId') || '';
  }

  getCurrentUser(): LoginResponse | null {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  }

  getToken(): string {
    return localStorage.getItem('authToken') || '';
  }
}