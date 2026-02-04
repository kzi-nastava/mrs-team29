import { CommonModule } from '@angular/common';
import { Component, DestroyRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService, LoginResponse } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isAuthenticated = false;
  currentUser: LoginResponse | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    destroyRef: DestroyRef
  ) {
    this.authService.isAuthenticated$
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe((value) => (this.isAuthenticated = value));

    this.authService.currentUser$
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe((user) => (this.currentUser = user));
  }

  get isAdmin(): boolean {
    return this.currentUser?.role === 'ADMIN';
  }

  get isDriver(): boolean {
    return !!this.currentUser?.isDriver || this.currentUser?.role === 'DRIVER';
  }

  get isClient(): boolean {
    return this.currentUser?.role === 'CLIENT';
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}

