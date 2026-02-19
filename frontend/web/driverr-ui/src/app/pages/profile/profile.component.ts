import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../../services/profile.service';
import { DriverService } from '../../services/driver.service';
import { AuthService } from '../../services/auth.service';

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
  userId = '';
  
  showPasswordChange = false;
  passwordMessage = '';
  passwordError = '';
  profileMessage = '';
  profileDefaults: any = null;
  userProfile: any = null;
  
  workingHours: number = 0;
  isDriver = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService,
    private driverService: DriverService,
    private authService: AuthService
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

    const currentUser = this.authService.getCurrentUser();
    this.userId = currentUser?.userId || '';

    if (currentUser) {
      this.profileForm.patchValue({
        firstName: currentUser.firstName,
        lastName: currentUser.lastName,
        email: currentUser.email
      });
      this.isDriver = currentUser.isDriver || currentUser.role === 'DRIVER';
    }

    if (this.userId) {
      this.loadProfile();
      this.loadWorkingHours();
    }
  }

  loadProfile() {
    this.profileService.getProfile(this.userId)
      .subscribe({
        next: (profile) => {
          this.userProfile = profile;
          const profileValues = {
            firstName: profile.firstName,
            lastName: profile.lastName,
            gender: profile.gender,
            username: profile.username || profile.userName,
            email: profile.email,
            phoneNumber: profile.phoneNumber,
            profilePictureUrl: profile.profilePictureUrl
          };
          this.profileForm.patchValue(profileValues);
          this.profileDefaults = { ...profileValues };
          this.isDriver = profile.isDriver || profile.driverId;
        },
        error: (error) => console.error('Failed to load profile:', error)
      });
  }

  loadWorkingHours() {
    if (!this.userId) {
      return;
    }

    this.driverService.getWorkingHours(this.userId).subscribe({
      next: (data) => {
        this.workingHours = data.workingHours || 0;
        this.isDriver = true;
      },
      error: () => {
        this.workingHours = 0;
        // leave isDriver as-is
      }
    });
  }

  saveProfile() {
    if (!this.userId) {
      this.profileMessage = 'User not loaded. Please log in again.';
      return;
    }
    this.loading = true;
    this.profileMessage = '';
    const payload = this.profileForm.getRawValue();
    this.profileService.updateProfile(this.userId, payload)
      .subscribe({
        next: () => {
          this.loading = false;
          this.profileMessage = 'Profile updated successfully!';
          this.profileDefaults = { ...payload };
          setTimeout(() => this.profileMessage = '', 3000);
        },
        error: (error) => {
          this.loading = false;
          this.profileMessage = error.error?.message || 'Failed to update profile';
        }
      });
  }

  resetProfileToDefault() {
    if (!this.profileDefaults) {
      return;
    }
    this.profileForm.reset({
      firstName: this.profileDefaults.firstName || '',
      lastName: this.profileDefaults.lastName || '',
      gender: this.profileDefaults.gender || '',
      username: this.profileDefaults.username || '',
      email: this.profileDefaults.email || '',
      phoneNumber: this.profileDefaults.phoneNumber || '',
      profilePictureUrl: this.profileDefaults.profilePictureUrl || ''
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


