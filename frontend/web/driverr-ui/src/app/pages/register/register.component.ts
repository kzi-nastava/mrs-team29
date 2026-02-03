import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  form = {
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: '',
    password: '',
    confirmPassword: ''
  };
  
  loading: boolean = false;
  error: string = '';
  success: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    // Validate form
    if (!this.form.firstName || !this.form.lastName || !this.form.email || 
        !this.form.phoneNumber || !this.form.address || 
        !this.form.password || !this.form.confirmPassword) {
      this.error = 'Please fill in all fields';
      return;
    }

    if (this.form.password !== this.form.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    if (this.form.password.length < 6) {
      this.error = 'Password must be at least 6 characters';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const registerData: RegisterRequest = {
      firstName: this.form.firstName,
      lastName: this.form.lastName,
      email: this.form.email,
      phoneNumber: this.form.phoneNumber,
      address: this.form.address,
      password: this.form.password,
      confirmPassword: this.form.confirmPassword
    };

    this.authService.register(registerData).subscribe({
      next: (response) => {
        this.loading = false;
        this.success = 'Registration successful! Please check your email to activate your account.';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
