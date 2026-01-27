import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {

  form!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    // TODO: kasnije ovo punimo iz backend-a
    const mockUser = {
      firstName: 'Nenad',
      lastName: 'Jevremović',
      email: 'nenad@example.com',
      username: 'nightowl',
      phoneNumber: '+381 64 123 456'
    };

    this.form = this.fb.group({
      firstName: [mockUser.firstName, Validators.required],
      lastName: [mockUser.lastName, Validators.required],
      email: [mockUser.email, [Validators.required, Validators.email]],
      username: [mockUser.username, Validators.required],
      phoneNumber: [mockUser.phoneNumber]
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    console.log('Updated profile:', this.form.value);

    // TODO: userService.updateProfile(this.form.value)
  }
}
