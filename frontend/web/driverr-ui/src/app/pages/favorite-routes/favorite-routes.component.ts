import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FavoriteRouteService } from '../../services/favorite-route.service';
import { MapService, GeocodeResult } from '../../services/map.service';
import { AuthService } from '../../services/auth.service';
import { RideService } from '../../services/ride.service';
import * as L from 'leaflet';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-favorite-routes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './favorite-routes.component.html',
  styleUrls: ['./favorite-routes.component.css']
})
export class FavoriteRoutesComponent implements OnInit, AfterViewInit {

  favoriteRoutes: any[] = [];
  form!: FormGroup;
  editingId: string | null = null;
  showForm = false;
  message = '';
  errorMessage = '';
  loading = false;
  saving = false;
  ordering = false;
  mapLoading = false;
  userId = '';
  hasActiveRide = false;

  // Map properties
  map!: L.Map;
  pickupMarker?: L.Marker;
  destinationMarker?: L.Marker;
  routeLine?: L.Polyline;
  pickupAddress?: GeocodeResult;
  destinationAddress?: GeocodeResult;
  searchQuery = '';
  selectingFor: 'pickup' | 'destination' | null = null;
  previewingRoute: any = null;
  previewMap?: L.Map;
  previewPickupMarker?: L.Marker;
  previewDestinationMarker?: L.Marker;
  previewRouteLine?: L.Polyline;

  constructor(
    private fb: FormBuilder,
    private favoriteRouteService: FavoriteRouteService,
    private mapService: MapService,
    private authService: AuthService,
    private rideService: RideService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.userId = this.authService.getUserId();
    if (!this.userId) {
      this.errorMessage = 'Please log in to manage favorite routes.';
      return;
    }
    this.loadFavoriteRoutes();
    this.checkActiveRide();
  }

  ngAfterViewInit() {
    if (this.showForm) {
      this.ensureMapReady();
    }
  }

  initializeMap() {
    if (this.map) {
      return;
    }
    this.map = L.map('favorites-map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const target = this.resolveSelectionTarget();
      this.selectingFor = target;
      this.selectLocationFromMap(e.latlng.lat, e.latlng.lng);
    });
  }

  initializeForm() {
    this.form = this.fb.group({
      name: ['', Validators.required],
      pickupAddressId: [''],
      destinationAddressId: [''],
      stops: ['']
    });
  }

  loadFavoriteRoutes() {
    if (!this.userId) {
      return;
    }
    this.loading = true;
    this.favoriteRouteService.getMyFavorites(this.userId).subscribe({
      next: (routes) => {
        this.favoriteRoutes = routes;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Failed to load favorite routes';
      }
    });
  }

  checkActiveRide() {
    if (!this.userId) {
      return;
    }
    this.rideService.hasActiveRide(this.userId).subscribe({
      next: (hasActive) => {
        this.hasActiveRide = hasActive;
      },
      error: () => {
        this.hasActiveRide = false;
      }
    });
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.form.reset();
      this.editingId = null;
      this.destroyMap();
    } else {
      this.ensureMapReady();
    }
  }

  saveFavoriteRoute() {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.message = '';

    const stops = this.form.get('stops')?.value 
      ? this.form.get('stops')?.value.split(',').map((s: string) => s.trim()).filter((s: string) => s)
      : [];

    if (!this.pickupAddress || !this.destinationAddress) {
      this.saving = false;
      this.errorMessage = 'Please select pickup and destination on the map';
      return;
    }

    forkJoin({
      pickup: this.mapService.saveAddress(this.pickupAddress),
      destination: this.mapService.saveAddress(this.destinationAddress)
    }).subscribe({
      next: ({ pickup, destination }) => {
        const routeData = {
          userId: this.userId,
          name: this.form.get('name')?.value,
          pickupAddressId: pickup.id,
          destinationAddressId: destination.id,
          stopAddressIds: []
        };

        if (this.editingId) {
          // Update existing
          this.favoriteRouteService.updateFavorite(this.editingId, routeData).subscribe({
            next: () => {
              this.saving = false;
              this.message = 'Route updated successfully!';
              this.loadFavoriteRoutes();
              this.form.reset();
              this.editingId = null;
              this.showForm = false;
              setTimeout(() => this.message = '', 3000);
            },
            error: (error) => {
              this.saving = false;
              this.errorMessage = error.error?.message || 'Failed to update route';
            }
          });
        } else {
          // Create new
          this.favoriteRouteService.createFavorite(routeData).subscribe({
            next: () => {
              this.saving = false;
              this.message = 'Favorite route created!';
              this.loadFavoriteRoutes();
              this.form.reset();
              this.showForm = false;
              setTimeout(() => this.message = '', 3000);
            },
            error: (error) => {
              this.saving = false;
              this.errorMessage = error.error?.message || 'Failed to create route';
            }
          });
        }
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Failed to save addresses for this route';
      }
    });
  }

  orderFromFavorite(routeId: string) {
    if (this.hasActiveRide) {
      this.errorMessage = 'You have an active ride. Complete or cancel it before ordering another.';
      return;
    }
    if (!this.userId) {
      this.errorMessage = 'Please log in to order a ride.';
      return;
    }

    this.ordering = true;
    this.errorMessage = '';
    this.message = '';
    this.favoriteRouteService.orderFromFavorite(routeId, this.userId).subscribe({
      next: () => {
        this.ordering = false;
        this.message = 'Ride ordered from favorite route!';
        this.checkActiveRide();
        setTimeout(() => this.message = '', 3000);
      },
      error: (error) => {
        this.ordering = false;
        this.errorMessage = error.error?.message || 'Failed to order from favorite route';
      }
    });
  }

  editRoute(route: any) {
    this.editingId = route.id;
    this.form.patchValue({
      name: route.name,
      pickupAddressId: route.pickupAddressId,
      destinationAddressId: route.destinationAddressId,
      stops: route.stops ? route.stops.join(', ') : ''
    });

    if (route.pickupAddress) {
      this.pickupAddress = {
        displayName: route.pickupAddress.displayName || route.pickupAddress.street,
        street: route.pickupAddress.street,
        streetNumber: route.pickupAddress.streetNumber,
        city: route.pickupAddress.city,
        postalCode: route.pickupAddress.postalCode,
        country: route.pickupAddress.country,
        latitude: route.pickupAddress.latitude,
        longitude: route.pickupAddress.longitude
      };
    }

    if (route.destinationAddress) {
      this.destinationAddress = {
        displayName: route.destinationAddress.displayName || route.destinationAddress.street,
        street: route.destinationAddress.street,
        streetNumber: route.destinationAddress.streetNumber,
        city: route.destinationAddress.city,
        postalCode: route.destinationAddress.postalCode,
        country: route.destinationAddress.country,
        latitude: route.destinationAddress.latitude,
        longitude: route.destinationAddress.longitude
      };
    }
    this.showForm = true;
  }

  deleteRoute(routeId: string) {
    if (confirm('Are you sure you want to delete this favorite route?')) {
      this.loading = true;
      this.favoriteRouteService.deleteFavorite(routeId).subscribe({
        next: () => {
          this.loading = false;
          this.message = 'Route deleted successfully!';
          this.loadFavoriteRoutes();
          setTimeout(() => this.message = '', 3000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Failed to delete route';
        }
      });
    }
  }

  cancelEdit() {
    this.editingId = null;
    this.form.reset();
    this.showForm = false;
    this.clearMap();
  }

  // Map methods
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

    this.mapLoading = true;
    const target = this.resolveSelectionTarget();
    this.selectingFor = target;
    this.mapService.geocode(this.searchQuery).subscribe({
      next: (address) => {
        this.mapLoading = false;

        if (target === 'pickup') {
          this.setPickupAddress(address);
        } else {
          this.setDestinationAddress(address);
        }

        this.map.setView([address.latitude, address.longitude], 15);
        this.searchQuery = '';
        this.selectingFor = null;
        this.errorMessage = '';
      },
      error: (err) => {
        this.mapLoading = false;
        this.errorMessage = 'Address not found. Try a different search.';
      }
    });
  }

  selectLocationFromMap(lat: number, lng: number) {
    this.mapLoading = true;
    const target = this.resolveSelectionTarget();
    this.mapService.reverseGeocode(lat, lng).subscribe({
      next: (address) => {
        this.mapLoading = false;

        if (target === 'pickup') {
          this.setPickupAddress(address);
        } else {
          this.setDestinationAddress(address);
        }

        this.selectingFor = null;
        this.errorMessage = '';
      },
      error: (err) => {
        this.mapLoading = false;
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

  setPickupAddress(address: GeocodeResult) {
    this.pickupAddress = address;
    this.form.patchValue({ pickupAddressId: '' });

    if (this.pickupMarker) {
      this.map.removeLayer(this.pickupMarker);
    }

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

    this.drawRoute();
  }

  setDestinationAddress(address: GeocodeResult) {
    this.destinationAddress = address;
    this.form.patchValue({ destinationAddressId: '' });

    if (this.destinationMarker) {
      this.map.removeLayer(this.destinationMarker);
    }

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

    this.drawRoute();
  }

  drawRoute() {
    if (!this.pickupAddress || !this.destinationAddress) {
      return;
    }

    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
    }

    this.routeLine = L.polyline([
      [this.pickupAddress.latitude, this.pickupAddress.longitude],
      [this.destinationAddress.latitude, this.destinationAddress.longitude]
    ], { color: '#2ec4b6', weight: 4 }).addTo(this.map);

    const bounds = L.latLngBounds([
      [this.pickupAddress.latitude, this.pickupAddress.longitude],
      [this.destinationAddress.latitude, this.destinationAddress.longitude]
    ]);
    this.map.fitBounds(bounds, { padding: [50, 50] });
  }

  clearPickup() {
    this.pickupAddress = undefined;
    this.form.patchValue({ pickupAddressId: '' });
    if (this.pickupMarker) {
      this.map.removeLayer(this.pickupMarker);
      this.pickupMarker = undefined;
    }
    this.clearRoute();
  }

  clearDestination() {
    this.destinationAddress = undefined;
    this.form.patchValue({ destinationAddressId: '' });
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
  }

  clearMap() {
    this.clearPickup();
    this.clearDestination();
    this.pickupAddress = undefined;
    this.destinationAddress = undefined;
  }

  private ensureMapReady() {
    if (!this.showForm) {
      return;
    }
    setTimeout(() => {
      if (!this.map) {
        this.initializeMap();
      } else {
        this.map.invalidateSize();
      }
    }, 0);
  }

  private destroyMap() {
    if (this.map) {
      this.map.remove();
      this.map = undefined as unknown as L.Map;
    }
    this.pickupMarker = undefined;
    this.destinationMarker = undefined;
    this.routeLine = undefined;
  }

  previewRoute(route: any) {
    this.previewingRoute = route;
    setTimeout(() => {
      this.ensurePreviewMapReady();
      this.clearPreviewMap();

      if (!this.previewMap) {
        return;
      }

      // Load pickup and destination addresses and display on map
      if (route.pickupAddress && route.destinationAddress) {
        const pickupAddr = route.pickupAddress;
        const destAddr = route.destinationAddress;

        const pickupIcon = L.icon({
          iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
          shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41],
          popupAnchor: [1, -34],
          shadowSize: [41, 41]
        });

        const destIcon = L.icon({
          iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
          shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41],
          popupAnchor: [1, -34],
          shadowSize: [41, 41]
        });

        this.previewPickupMarker = L.marker([pickupAddr.latitude, pickupAddr.longitude], { icon: pickupIcon })
          .addTo(this.previewMap)
          .bindPopup(`<b>Pickup:</b><br>${pickupAddr.street || 'Unknown'}`);

        this.previewDestinationMarker = L.marker([destAddr.latitude, destAddr.longitude], { icon: destIcon })
          .addTo(this.previewMap)
          .bindPopup(`<b>Destination:</b><br>${destAddr.street || 'Unknown'}`);

        // Draw route line
        this.previewRouteLine = L.polyline([
          [pickupAddr.latitude, pickupAddr.longitude],
          [destAddr.latitude, destAddr.longitude]
        ], { color: '#2ec4b6', weight: 4 }).addTo(this.previewMap);

        // Fit bounds
        const bounds = L.latLngBounds([
          [pickupAddr.latitude, pickupAddr.longitude],
          [destAddr.latitude, destAddr.longitude]
        ]);
        this.previewMap.fitBounds(bounds, { padding: [50, 50] });
      }
    }, 0);
  }

  closePreview() {
    this.previewingRoute = null;
    this.clearPreviewMap();
  }

  private ensurePreviewMapReady() {
    if (!this.previewMap) {
      this.previewMap = L.map('favorites-preview-map').setView([45.2671, 19.8335], 13);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
      }).addTo(this.previewMap);
    } else {
      this.previewMap.invalidateSize();
    }
  }

  private clearPreviewMap() {
    if (this.previewPickupMarker && this.previewMap) {
      this.previewMap.removeLayer(this.previewPickupMarker);
      this.previewPickupMarker = undefined;
    }
    if (this.previewDestinationMarker && this.previewMap) {
      this.previewMap.removeLayer(this.previewDestinationMarker);
      this.previewDestinationMarker = undefined;
    }
    if (this.previewRouteLine && this.previewMap) {
      this.previewMap.removeLayer(this.previewRouteLine);
      this.previewRouteLine = undefined;
    }
  }
}
