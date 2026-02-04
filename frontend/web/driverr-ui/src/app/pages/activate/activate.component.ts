import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-activate',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="activation-container">
      <div class="activation-card">
        <h1 *ngIf="loading">Activating your account...</h1>
        <h1 *ngIf="!loading && success" class="success">Account Activated Successfully!</h1>
        <h1 *ngIf="!loading && !success" class="error">Activation Failed</h1>
        
        <p *ngIf="loading" class="loading-text">Please wait while we verify your email...</p>
        <p *ngIf="!loading && success" class="success-message">
          Your account has been activated. You can now log in with your credentials.
        </p>
        <p *ngIf="!loading && !success" class="error-message">{{ errorMessage }}</p>
        
        <button 
          *ngIf="!loading" 
          (click)="redirectToLogin()" 
          class="btn-primary">
          Go to Login
        </button>
      </div>
    </div>
  `,
  styles: [`
    .activation-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .activation-card {
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
      text-align: center;
      max-width: 500px;
      width: 90%;
    }

    h1 {
      margin-bottom: 20px;
      font-size: 28px;
      color: #333;
    }

    h1.success {
      color: #27ae60;
    }

    h1.error {
      color: #e74c3c;
    }

    p {
      margin: 15px 0;
      font-size: 16px;
      color: #555;
      line-height: 1.6;
    }

    .loading-text {
      color: #667eea;
      font-weight: 500;
    }

    .success-message {
      color: #27ae60;
    }

    .error-message {
      color: #e74c3c;
    }

    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 12px 30px;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      cursor: pointer;
      margin-top: 20px;
      transition: transform 0.3s ease;
    }

    .btn-primary:hover {
      transform: translateY(-2px);
    }
  `]
})
export class ActivateComponent implements OnInit {
  loading = true;
  success = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.activateAccount(token);
      } else {
        this.loading = false;
        this.errorMessage = 'Invalid activation link. No token provided.';
      }
    });
  }

  activateAccount(token: string) {
    this.authService.activateAccount(token).subscribe(
      (response) => {
        this.loading = false;
        this.success = true;
      },
      (error) => {
        this.loading = false;
        this.success = false;
        this.errorMessage = error.error?.message || 'Account activation failed. Please try again.';
      }
    );
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
