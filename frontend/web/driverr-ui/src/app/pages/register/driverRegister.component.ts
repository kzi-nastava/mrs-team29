import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { DriverService } from '../../services/driver.service';

@Component({
  selector: 'app-admin-driver-register',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './driverRegister.component.html',
  styleUrls: ['./register.component.css']
})
export class AdminDriverRegisterComponent {

  form: FormGroup;

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
    this.driverService.registerDriver(this.form.value)
      .subscribe({
        next: () => alert('Driver registered successfully'),
        error: () => alert('Error registering driver')
      });
  }
}