import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  quickLogin(): void {
    this.authService.quickLogin();
    this.router.navigate(['/order-ride']);
  }

  mockLogin(): void {
    this.authService.login('test@test.com', 'password').subscribe(() => {
      this.router.navigate(['/order-ride']);
    });
  }
}
