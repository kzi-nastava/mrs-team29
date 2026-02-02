import { Component, OnInit } from '@angular/core';
import { RideService } from '../../services/ride.service';
import { FavoriteRouteService } from '../../services/favorite-route.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-order-ride',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './ride-order.component.html',
  styleUrls: ['./ride-order.component.css']
})
export class OrderRideComponent implements OnInit {

  rideForm!: FormGroup;
  creatorId = 'USER_ID_123'; // later from auth
  
  showFavorites = false;
  favoriteRoutes: any[] = [];
  
  stops: string[] = [];
  message = '';
  errorMessage = '';
  loading = false;
  hasActiveRide = false;

  vehicleTypes = [
    { value: 'STANDARD', label: 'Standard Car' },
    { value: 'LUXURY', label: 'Luxury Car' },
    { value: 'VAN', label: 'Van' }
  ];

  constructor(
    private fb: FormBuilder,
    private rideService: RideService,
    private favoriteRouteService: FavoriteRouteService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.checkActiveRide();
    this.loadFavoriteRoutes();
  }

  initializeForm() {
    this.rideForm = this.fb.group({
      pickupAddressId: ['', Validators.required],
      destinationAddressId: ['', Validators.required],
      vehicleType: ['STANDARD', Validators.required],
      scheduledTime: [''], // optional for immediate rides
      passengerIds: [''],
      pets: [false],
      baby: [false],
      notes: ['']
    });
  }

  checkActiveRide() {
    this.rideService.hasActiveRide(this.creatorId).subscribe({
      next: (hasActive) => {
        this.hasActiveRide = hasActive;
        if (hasActive) {
          this.errorMessage = 'You have an active ride. Complete or cancel it before ordering another.';
        }
      },
      error: () => {
        this.hasActiveRide = false;
      }
    });
  }

  loadFavoriteRoutes() {
    this.favoriteRouteService.getMyFavorites(this.creatorId).subscribe({
      next: (routes) => {
        this.favoriteRoutes = routes;
      },
      error: () => {
        this.favoriteRoutes = [];
      }
    });
  }

  addStop() {
    this.stops.push('');
  }

  removeStop(index: number) {
    this.stops.splice(index, 1);
  }

  orderRide() {
    // Check for active ride first
    if (this.hasActiveRide) {
      this.errorMessage = 'You have an active ride. Complete or cancel it before ordering another.';
      return;
    }

    if (this.rideForm.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    const passengerEmails = this.rideForm.get('passengerIds')?.value;
    const passengerIds = passengerEmails ? passengerEmails.split(',').map((e: string) => e.trim()) : [];

    const dto = {
      creatorId: this.creatorId,
      pickupAddressId: this.rideForm.get('pickupAddressId')?.value,
      destinationAddressId: this.rideForm.get('destinationAddressId')?.value,
      vehicleType: this.rideForm.get('vehicleType')?.value,
      scheduledTime: this.rideForm.get('scheduledTime')?.value || null,
      passengerIds: passengerIds,
      stops: this.stops.map(s => ({ street: s })),
      pets: this.rideForm.get('pets')?.value,
      baby: this.rideForm.get('baby')?.value,
      notes: this.rideForm.get('notes')?.value
    };

    this.rideService.orderRide(dto).subscribe({
      next: (res) => {
        this.loading = false;
        this.message = `Ride ordered! Price: ${res.price} RSD. Driver will arrive shortly.`;
        this.rideForm.reset();
        this.stops = [];
        this.checkActiveRide(); // Refresh active ride status
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Failed to order ride';
      }
    });
  }

  orderFromFavorite(routeId: string) {
    if (this.hasActiveRide) {
      this.errorMessage = 'You have an active ride. Complete or cancel it before ordering another.';
      return;
    }

    this.loading = true;
    this.favoriteRouteService.orderFromFavorite(routeId, this.creatorId).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.message = `Ride ordered from favorite! Price: ${res.price || 'TBD'} RSD`;
        this.showFavorites = false;
        this.checkActiveRide();
      },
      error: (err: any) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Failed to order from favorite';
      }
    });
  }

  toggleFavoritesPopup() {
    this.showFavorites = !this.showFavorites;
  }
}
