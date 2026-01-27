import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-ride-order',
  standalone: true,
  templateUrl: './ride-order.component.html',
  styleUrls: ['./ride-order.component.css']
})
export class RideOrderComponent {

  showFavorites = false;

  favoriteRoutes = [
    {
      start: 'Faculty of Technical Sciences',
      destination: 'Train station',
      stops: ['Boulevard Europe']
    },
    {
      start: 'Home',
      destination: 'City center',
      stops: []
    }
  ];

  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      start: [''],
      destination: [''],
      stops: this.fb.array([]),
      vehicleType: ['STANDARD'],
      baby: [false],
      pet: [false],
      scheduledAt: ['']
    });
  }

  get stops() {
    return this.form.get('stops') as FormArray;
  }

  addStop() {
    this.stops.push(this.fb.control(''));
  }

  removeStop(index: number) {
    this.stops.removeAt(index);
  }

  submit() {
    console.log('Ride order:', this.form.value);
    alert('Ride ordered (mock)');
  }

  openFavorites() {
    this.showFavorites = true;
  }

  closeFavorites() {
    this.showFavorites = false;
  }

  selectRoute(route: any) {
    this.form.patchValue({
      start: route.start,
      destination: route.destination
    });

    this.stops.clear();
    route.stops.forEach((s: string) => this.stops.push(this.fb.control(s)));

    this.closeFavorites();
  }
}
