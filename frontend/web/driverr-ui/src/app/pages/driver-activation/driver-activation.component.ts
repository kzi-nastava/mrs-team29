import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DriverService } from '../../services/driver.service';

@Component({
  selector: 'app-driver-activation',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './driver-activation.component.html',
  styleUrls: ['./driver-activation.component.css']
})
export class DriverActivationComponent implements OnInit {

  form!: FormGroup;
  activationToken: string = '';
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private driverService: DriverService
  ) {}

  ngOnInit() {
    // Get activation token from URL parameter (e.g., ?token=abc123)
    this.route.queryParams.subscribe(params => {
      this.activationToken = params['token'] || '';
      if (!this.activationToken) {
        this.errorMessage = 'Invalid activation link. No token provided.';
      }
    });

    this.form = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6)]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ 'passwordMismatch': true });
      return { 'passwordMismatch': true };
    }
    return null;
  }

  activate() {
    if (this.form.invalid || !this.activationToken) {
      this.errorMessage = 'Please fill in all fields correctly and have a valid token.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const activationDto = {
      token: this.activationToken,
      password: this.form.get('password')?.value,
      confirmPassword: this.form.get('confirmPassword')?.value
    };

    this.driverService.activateDriver(activationDto).subscribe({
      next: (response) => {
        this.successMessage = 'Account activated successfully! Redirecting to login...';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Activation failed. Please try again.';
      }
    });
  }
}
