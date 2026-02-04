import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="reset-password-container">
      <div class="reset-password-card">
        <h1>Reset Your Password</h1>

        <div *ngIf="tokenValid">
          <div *ngIf="errorMessage" class="alert alert-error">
            {{ errorMessage }}
          </div>

          <form (ngSubmit)="onSubmit()">
            <div class="form-group">
              <label for="newPassword">New Password</label>
              <input
                type="password"
                id="newPassword"
                [(ngModel)]="newPassword"
                name="newPassword"
                placeholder="Enter new password (min 6 characters)"
                required
                minlength="6"
                class="form-control"
              />
              <small *ngIf="newPassword && newPassword.length < 6" class="error-text">
                Password must be at least 6 characters
              </small>
            </div>

            <div class="form-group">
              <label for="confirmPassword">Confirm Password</label>
              <input
                type="password"
                id="confirmPassword"
                [(ngModel)]="confirmPassword"
                name="confirmPassword"
                placeholder="Confirm new password"
                required
                class="form-control"
              />
              <small *ngIf="newPassword && confirmPassword && newPassword !== confirmPassword" class="error-text">
                Passwords do not match
              </small>
            </div>

            <button
              type="submit"
              class="btn-primary"
              [disabled]="loading || newPassword.length < 6 || newPassword !== confirmPassword"
            >
              {{ loading ? 'Resetting...' : 'Reset Password' }}
            </button>
          </form>
        </div>

        <div *ngIf="!tokenValid">
          <div class="alert alert-error">
            {{ errorMessage || 'Invalid password reset token.' }}
          </div>
          <button (click)="redirectToLogin()" class="btn-primary">
            Back to Login
          </button>
        </div>

        <div *ngIf="successMessage" class="alert alert-success">
          {{ successMessage }}
          <button (click)="redirectToLogin()" class="btn-primary" style="margin-top: 15px;">
            Go to Login
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .reset-password-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .reset-password-card {
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
      margin-bottom: 30px;
      font-size: 28px;
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

    small {
      display: block;
      margin-top: 5px;
      font-size: 12px;
    }

    .error-text {
      color: #e74c3c;
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
export class ResetPasswordComponent implements OnInit {
  newPassword = '';
  confirmPassword = '';
  token = '';
  loading = false;
  tokenValid = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      if (this.token) {
        this.tokenValid = true;
      } else {
        this.tokenValid = false;
        this.errorMessage = 'Invalid password reset link.';
      }
    });
  }

  onSubmit() {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.resetPassword(this.token, this.newPassword, this.confirmPassword).subscribe(
      (response) => {
        this.loading = false;
        this.successMessage = 'Password reset successfully! Redirecting to login...';
        setTimeout(() => this.redirectToLogin(), 2000);
      },
      (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Failed to reset password. Please try again.';
      }
    );
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
