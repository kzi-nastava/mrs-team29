import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../../services/profile.service';
import { DriverService } from '../../services/driver.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  userId = 'HARDCODED_ID_ZA_SADA'; // kasnije iz auth-a
  
  showPasswordChange = false;
  passwordMessage = '';
  passwordError = '';
  profileMessage = '';
  
  workingHours: number = 0;
  isDriver = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService,
    private driverService: DriverService
  ) {}

  ngOnInit() {
    this.profileForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      gender: [''],
      username: [{ value: '', disabled: true }],
      email: [{ value: '', disabled: true }],
      phoneNumber: [''],
      profilePictureUrl: ['']
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required, Validators.minLength(6)]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6)]]
    }, { validators: this.passwordMatchValidator });

    this.loadProfile();
    this.loadWorkingHours();
  }

  loadProfile() {
    this.profileService.getProfile(this.userId)
      .subscribe({
        next: (profile) => {
          this.profileForm.patchValue(profile);
          this.isDriver = profile.isDriver || profile.driverId;
        },
        error: (error) => console.error('Failed to load profile:', error)
      });
  }

  loadWorkingHours() {
    this.driverService.getWorkingHours(this.userId).subscribe({
      next: (data) => {
        this.workingHours = data.workingHours || 0;
        this.isDriver = true;
      },
      error: () => {
        this.workingHours = 0;
        this.isDriver = false;
      }
    });
  }

  saveProfile() {
    this.loading = true;
    this.profileMessage = '';
    this.profileService.updateProfile(this.userId, this.profileForm.getRawValue())
      .subscribe({
        next: () => {
          this.loading = false;
          this.profileMessage = 'Profile updated successfully!';
          setTimeout(() => this.profileMessage = '', 3000);
        },
        error: (error) => {
          this.loading = false;
          this.profileMessage = error.error?.message || 'Failed to update profile';
        }
      });
  }

  togglePasswordChange() {
    this.showPasswordChange = !this.showPasswordChange;
    this.passwordForm.reset();
    this.passwordMessage = '';
    this.passwordError = '';
  }

  changePassword() {
    if (this.passwordForm.invalid) {
      this.passwordError = 'Please fill in all fields correctly';
      return;
    }

    if (this.passwordForm.value.newPassword !== this.passwordForm.value.confirmPassword) {
      this.passwordError = 'Passwords do not match';
      return;
    }

    this.loading = true;
    this.passwordError = '';
    this.passwordMessage = '';

    const passwordDto = {
      oldPassword: this.passwordForm.get('oldPassword')?.value,
      newPassword: this.passwordForm.get('newPassword')?.value,
      confirmPassword: this.passwordForm.get('confirmPassword')?.value
    };

    this.profileService.changePassword(this.userId, passwordDto).subscribe({
      next: () => {
        this.loading = false;
        this.passwordMessage = 'Password changed successfully!';
        this.passwordForm.reset();
        setTimeout(() => {
          this.showPasswordChange = false;
          this.passwordMessage = '';
        }, 2000);
      },
      error: (error) => {
        this.loading = false;
        this.passwordError = error.error?.message || 'Failed to change password';
      }
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');
    
    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ 'passwordMismatch': true });
      return { 'passwordMismatch': true };
    }
    return null;
  }
}


