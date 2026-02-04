import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-request-password-reset',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="password-reset-container">
      <div class="password-reset-card">
        <h1>Reset Password</h1>
        <p class="subtitle">Enter your email address and we'll send you a link to reset your password.</p>

        <div *ngIf="successMessage" class="alert alert-success">
          {{ successMessage }}
        </div>

        <div *ngIf="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>

        <form (ngSubmit)="onSubmit()" *ngIf="!submitted">
          <div class="form-group">
            <label for="email">Email Address</label>
            <input
              type="email"
              id="email"
              [(ngModel)]="email"
              name="email"
              placeholder="Enter your email"
              required
              class="form-control"
            />
          </div>

          <button type="submit" class="btn-primary" [disabled]="loading">
            {{ loading ? 'Sending...' : 'Send Reset Link' }}
          </button>
        </form>

        <button *ngIf="submitted" (click)="redirectToLogin()" class="btn-primary">
          Back to Login
        </button>
      </div>
    </div>
  `,
  styles: [`
    .password-reset-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .password-reset-card {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
      max-width: 450px;
      width: 90%;
    }

    h1 {
      text-align: center;
      color: #333;
      margin-bottom: 10px;
      font-size: 28px;
    }

    .subtitle {
      text-align: center;
      color: #666;
      margin-bottom: 20px;
      font-size: 14px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    label {
      display: block;
      margin-bottom: 8px;
      color: #333;
      font-weight: 500;
    }

    .form-control {
      width: 100%;
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 14px;
      box-sizing: border-box;
      transition: border-color 0.3s;
    }

    .form-control:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .btn-primary {
      width: 100%;
      padding: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      font-weight: 500;
      cursor: pointer;
      transition: transform 0.3s ease;
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
    }

    .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .alert {
      padding: 12px;
      margin-bottom: 20px;
      border-radius: 5px;
      font-size: 14px;
    }

    .alert-success {
      background: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }

    .alert-error {
      background: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }
  `]
})
export class RequestPasswordResetComponent {
  email = '';
  loading = false;
  submitted = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.email) {
      this.errorMessage = 'Please enter your email address';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.requestPasswordReset(this.email).subscribe(
      (response) => {
        this.loading = false;
        this.submitted = true;
        this.successMessage = 'Password reset link has been sent to your email. Please check your inbox.';
      },
      (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Failed to send reset link. Please try again.';
      }
    );
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
