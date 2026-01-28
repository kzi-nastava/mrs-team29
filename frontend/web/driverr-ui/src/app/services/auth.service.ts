import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor() {
    // Check if user is already logged in (from localStorage)
    const token = localStorage.getItem('authToken');
    if (token) {
      this.isAuthenticatedSubject.next(true);
    }
  }

  // Mock login
  login(email: string, password: string): Observable<boolean> {
    return new Observable(observer => {
      setTimeout(() => {
        // Mock authentication - accept any credentials for testing
        const mockToken = 'mock_token_' + Date.now();
        localStorage.setItem('authToken', mockToken);
        localStorage.setItem('userId', 'USER_' + Date.now());
        this.isAuthenticatedSubject.next(true);
        observer.next(true);
        observer.complete();
      }, 500);
    });
  }

  // Quick login for testing (no credentials needed)
  quickLogin(): void {
    const mockToken = 'mock_token_' + Date.now();
    localStorage.setItem('authToken', mockToken);
    localStorage.setItem('userId', 'USER_123');
    this.isAuthenticatedSubject.next(true);
  }

  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
    this.isAuthenticatedSubject.next(false);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }

  getUserId(): string {
    return localStorage.getItem('userId') || 'USER_123';
  }
}