import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  form!: FormGroup;
  userId = 'HARDCODED_ID_ZA_SADA'; // kasnije iz auth-a

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      firstName: [''],
      lastName: [''],
      gender: [''],
      username: [{ value: '', disabled: true }],
      email: [{ value: '', disabled: true }],
      phoneNumber: [''],
      profilePictureUrl: ['']
    });

    this.profileService.getProfile(this.userId)
      .subscribe(profile => this.form.patchValue(profile));
  }

  save() {
    this.profileService.updateProfile(this.userId, this.form.getRawValue())
      .subscribe(() => alert('Profile saved'));
  }
}

