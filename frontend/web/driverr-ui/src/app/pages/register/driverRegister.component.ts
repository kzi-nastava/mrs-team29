import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
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
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      gender: [''],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      phoneNumber: [''],
      vehicleModel: ['', Validators.required],
      vehicleType: ['STANDARD', Validators.required],
      registrationPlate: ['', Validators.required],
      seats: [4, [Validators.required, Validators.min(1)]],
      allowsPets: [false],
      allowsBabies: [false]
    });
  }

  submit() {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in all required fields';
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