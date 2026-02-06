import { Component, OnInit, AfterViewInit } from '@angular/core';
import { RideService } from '../../services/ride.service';
import { FavoriteRouteService } from '../../services/favorite-route.service';
import { MapService, AddressResponse, RouteResult } from '../../services/map.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as L from 'leaflet';

@Component({
  selector: 'app-order-ride',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './ride-order.component.html',
  styleUrls: ['./ride-order.component.css']
})
export class OrderRideComponent implements OnInit, AfterViewInit {

  rideForm!: FormGroup;
  creatorId = '';
  
  showFavorites = false;
  favoriteRoutes: any[] = [];
  
  stops: string[] = [];
  message = '';
  errorMessage = '';
  loading = false;
  hasActiveRide = false;

  // Map properties
  map!: L.Map;
  pickupMarker?: L.Marker;
  destinationMarker?: L.Marker;
  routeLine?: L.Polyline;
  pickupAddress?: AddressResponse;
  destinationAddress?: AddressResponse;
  searchQuery = '';
  searchResults: AddressResponse[] = [];
  selectingFor: 'pickup' | 'destination' | null = null;
  routeInfo?: RouteResult;
  estimatedPrice = 0;

  vehicleTypes = [
    { value: 'STANDARD', label: 'Standard Car' },
    { value: 'LUXURY', label: 'Luxury Car' },
    { value: 'VAN', label: 'Van' }
  ];

  constructor(
    private fb: FormBuilder,
    private rideService: RideService,
    private favoriteRouteService: FavoriteRouteService,
    private mapService: MapService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.creatorId = this.authService.getUserId();
    if (!this.creatorId) {
      this.errorMessage = 'Please log in to order a ride.';
      return;
    }
    this.checkActiveRide();
    this.loadFavoriteRoutes();
  }

  ngAfterViewInit() {
    this.initializeMap();
  }

  initializeMap() {
    // Default center: Novi Sad, Serbia
    this.map = L.map('map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    // Click on map to select location
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const target = this.resolveSelectionTarget();
      this.selectingFor = target;
      this.selectLocationFromMap(e.latlng.lat, e.latlng.lng);
    });
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
    if (!this.creatorId) {
      return;
    }
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
    if (!this.creatorId) {
      return;
    }
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
    if (!this.creatorId) {
      this.errorMessage = 'Please log in to order a ride.';
      return;
    }
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
      stopAddressIds: [],
      vehicleType: this.rideForm.get('vehicleType')?.value,
      scheduledTime: this.rideForm.get('scheduledTime')?.value || null,
      passengerIds: passengerIds,
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
    if (!this.creatorId) {
      this.errorMessage = 'Please log in to order a ride.';
      return;
    }
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

  // Map-related methods
  startSelectingPickup() {
    this.selectingFor = 'pickup';
    this.errorMessage = 'Click on the map or search for a pickup location';
  }

  startSelectingDestination() {
    this.selectingFor = 'destination';
    this.errorMessage = 'Click on the map or search for a destination';
  }

  searchAddress() {
    if (!this.searchQuery.trim()) {
      return;
    }

    this.loading = true;
    const target = this.resolveSelectionTarget();
    this.selectingFor = target;
    this.mapService.geocodeAndSave(this.searchQuery).subscribe({
      next: (address) => {
        this.loading = false;

        if (target === 'pickup') {
          this.setPickupAddress(address);
        } else {
          this.setDestinationAddress(address);
        }

        // Move map to location
        this.map.setView([address.latitude, address.longitude], 15);
        this.searchQuery = '';
        this.selectingFor = null;
        this.clearErrorMessageDeferred();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Address not found. Try a different search.';
      }
    });
  }

  selectLocationFromMap(lat: number, lng: number) {
    this.loading = true;
    const target = this.resolveSelectionTarget();
    this.mapService.reverseGeocodeAndSave(lat, lng).subscribe({
      next: (address) => {
        this.loading = false;

        if (target === 'pickup') {
          this.setPickupAddress(address);
        } else {
          this.setDestinationAddress(address);
        }

        this.selectingFor = null;
        this.clearErrorMessageDeferred();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Could not get address for this location';
      }
    });
  }

  private resolveSelectionTarget(): 'pickup' | 'destination' {
    if (this.selectingFor) {
      return this.selectingFor;
    }
    if (!this.pickupAddress) {
      return 'pickup';
    }
    if (!this.destinationAddress) {
      return 'destination';
    }
    return 'pickup';
  }

  setPickupAddress(address: AddressResponse) {
    this.pickupAddress = address;
    this.rideForm.patchValue({ pickupAddressId: address.id });

    // Remove old marker
    if (this.pickupMarker) {
      this.map.removeLayer(this.pickupMarker);
    }

    // Add new marker
    const icon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    this.pickupMarker = L.marker([address.latitude, address.longitude], { icon })
      .addTo(this.map)
      .bindPopup(`<b>Pickup:</b><br>${address.displayName || address.street}`);

    this.calculateRoute();
  }

  setDestinationAddress(address: AddressResponse) {
    this.destinationAddress = address;
    this.rideForm.patchValue({ destinationAddressId: address.id });

    // Remove old marker
    if (this.destinationMarker) {
      this.map.removeLayer(this.destinationMarker);
    }

    // Add new marker
    const icon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    this.destinationMarker = L.marker([address.latitude, address.longitude], { icon })
      .addTo(this.map)
      .bindPopup(`<b>Destination:</b><br>${address.displayName || address.street}`);

    this.calculateRoute();
  }

  calculateRoute() {
    if (!this.pickupAddress || !this.destinationAddress) {
      return;
    }

    this.mapService.getRoute(
      this.pickupAddress.latitude,
      this.pickupAddress.longitude,
      this.destinationAddress.latitude,
      this.destinationAddress.longitude
    ).subscribe({
      next: (route) => {
        this.routeInfo = route;
        
        // Calculate estimated price (base price + distance-based pricing)
        const vehicleType = this.rideForm.get('vehicleType')?.value;
        let basePrice = 200;
        if (vehicleType === 'LUXURY') basePrice = 400;
        if (vehicleType === 'VAN') basePrice = 300;
        
        const distanceKm = route.distanceMeters / 1000;
        this.estimatedPrice = Math.round(basePrice + (distanceKm * 120));

        // Draw route line on map
        if (this.routeLine) {
          this.map.removeLayer(this.routeLine);
        }

        this.routeLine = L.polyline([
          [this.pickupAddress!.latitude, this.pickupAddress!.longitude],
          [this.destinationAddress!.latitude, this.destinationAddress!.longitude]
        ], { color: '#2ec4b6', weight: 4 }).addTo(this.map);

        // Fit map to show both markers
        const bounds = L.latLngBounds([
          [this.pickupAddress!.latitude, this.pickupAddress!.longitude],
          [this.destinationAddress!.latitude, this.destinationAddress!.longitude]
        ]);
        this.map.fitBounds(bounds, { padding: [50, 50] });
      },
      error: (err) => {
        console.error('Route calculation failed', err);
      }
    });
  }

  clearPickup() {
    this.pickupAddress = undefined;
    this.rideForm.patchValue({ pickupAddressId: '' });
    if (this.pickupMarker) {
      this.map.removeLayer(this.pickupMarker);
      this.pickupMarker = undefined;
    }
    this.clearRoute();
  }

  clearDestination() {
    this.destinationAddress = undefined;
    this.rideForm.patchValue({ destinationAddressId: '' });
    if (this.destinationMarker) {
      this.map.removeLayer(this.destinationMarker);
      this.destinationMarker = undefined;
    }
    this.clearRoute();
  }

  clearRoute() {
    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
      this.routeLine = undefined;
    }
    this.routeInfo = undefined;
    this.estimatedPrice = 0;
  }

  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}m ${secs}s`;
  }

  formatDistance(meters: number): string {
    const km = (meters / 1000).toFixed(2);
    return `${km} km`;
  }

  private clearErrorMessageDeferred() {
    setTimeout(() => {
      this.errorMessage = '';
    }, 0);
  }
}
