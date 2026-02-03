import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DriverService } from '../../services/driver.service';

@Component({
  selector: 'app-admin-driver-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './driverRegister.component.html',
  styleUrls: ['./register.component.css']
})
export class AdminDriverRegisterComponent {

  form: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private driverService: DriverService
  ) {
    this.form = this.fb.group({
      firstName: [''],
      lastName: [''],
      gender: [''],
      username: [''],
      email: [''],
      password: [''],
      phoneNumber: [''],
      vehicleModel: [''],
      vehicleType: [''],
      registrationPlate: [''],
      seats: [4],
      allowsPets: [false],
      allowsBabies: [false]
    });
  }

  submit() {
    if (!this.form.value.email || !this.form.value.password) {
      this.errorMessage = 'Email and password are required';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.driverService.registerDriver(this.form.value)
      .subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = 'Driver registered successfully! Activation email sent to ' + this.form.value.email;
          this.form.reset();
          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Error registering driver';
        }
      });
  }
}