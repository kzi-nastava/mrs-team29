import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FavoriteRouteService } from '../../services/favorite-route.service';
import { MapService, AddressResponse } from '../../services/map.service';
import * as L from 'leaflet';

@Component({
  selector: 'app-favorite-routes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
  userId = 'USER_ID_123'; // later from auth

  // Map properties
  map!: L.Map;
  pickupMarker?: L.Marker;
  destinationMarker?: L.Marker;
  routeLine?: L.Polyline;
  pickupAddress?: AddressResponse;
  destinationAddress?: AddressResponse;
  searchQuery = '';
  selectingFor: 'pickup' | 'destination' | null = null;
  previewingRoute: any = null;

  constructor(
    private fb: FormBuilder,
    private favoriteRouteService: FavoriteRouteService,
    private mapService: MapService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadFavoriteRoutes();
  }

  ngAfterViewInit() {
    this.initializeMap();
  }

  initializeMap() {
    this.map = L.map('favorites-map').setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      if (this.selectingFor) {
        this.selectLocationFromMap(e.latlng.lat, e.latlng.lng);
      }
    });
  }

  initializeForm() {
    this.form = this.fb.group({
      name: ['', Validators.required],
      pickupAddressId: ['', Validators.required],
      destinationAddressId: ['', Validators.required],
      stops: ['']
    });
  }

  loadFavoriteRoutes() {
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

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.form.reset();
      this.editingId = null;
    }
  }

  saveFavoriteRoute() {
    if (this.form.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    const stops = this.form.get('stops')?.value 
      ? this.form.get('stops')?.value.split(',').map((s: string) => s.trim()).filter((s: string) => s)
      : [];

    const routeData = {
      clientId: this.userId,
      name: this.form.get('name')?.value,
      pickupAddressId: this.form.get('pickupAddressId')?.value,
      destinationAddressId: this.form.get('destinationAddressId')?.value,
      stops: stops
    };

    if (this.editingId) {
      // Update existing
      this.favoriteRouteService.updateFavorite(this.editingId, routeData).subscribe({
        next: () => {
          this.loading = false;
          this.message = 'Route updated successfully!';
          this.loadFavoriteRoutes();
          this.form.reset();
          this.editingId = null;
          this.showForm = false;
          setTimeout(() => this.message = '', 3000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Failed to update route';
        }
      });
    } else {
      // Create new
      this.favoriteRouteService.createFavorite(routeData).subscribe({
        next: () => {
          this.loading = false;
          this.message = 'Favorite route created!';
          this.loadFavoriteRoutes();
          this.form.reset();
          this.showForm = false;
          setTimeout(() => this.message = '', 3000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Failed to create route';
        }
      });
    }
  }

  editRoute(route: any) {
    this.editingId = route.id;
    this.form.patchValue({
      name: route.name,
      pickupAddressId: route.pickupAddressId,
      destinationAddressId: route.destinationAddressId,
      stops: route.stops ? route.stops.join(', ') : ''
    });
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

    this.loading = true;
    this.mapService.geocodeAndSave(this.searchQuery).subscribe({
      next: (address) => {
        this.loading = false;
        
        if (this.selectingFor === 'pickup') {
          this.setPickupAddress(address);
        } else if (this.selectingFor === 'destination') {
          this.setDestinationAddress(address);
        }

        this.map.setView([address.latitude, address.longitude], 15);
        this.searchQuery = '';
        this.selectingFor = null;
        this.errorMessage = '';
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Address not found. Try a different search.';
      }
    });
  }

  selectLocationFromMap(lat: number, lng: number) {
    this.loading = true;
    this.mapService.reverseGeocodeAndSave(lat, lng).subscribe({
      next: (address) => {
        this.loading = false;
        
        if (this.selectingFor === 'pickup') {
          this.setPickupAddress(address);
        } else if (this.selectingFor === 'destination') {
          this.setDestinationAddress(address);
        }

        this.selectingFor = null;
        this.errorMessage = '';
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Could not get address for this location';
      }
    });
  }

  setPickupAddress(address: AddressResponse) {
    this.pickupAddress = address;
    this.form.patchValue({ pickupAddressId: address.id });

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

  setDestinationAddress(address: AddressResponse) {
    this.destinationAddress = address;
    this.form.patchValue({ destinationAddressId: address.id });

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
    this.previewingRoute = null;
  }

  previewRoute(route: any) {
    this.previewingRoute = route;
    this.clearMap();
    
    // Load pickup and destination addresses and display on map
    if (route.pickupAddress && route.destinationAddress) {
      const pickupAddr = route.pickupAddress;
      const destAddr = route.destinationAddress;

      // Set markers
      if (this.pickupMarker) {
        this.map.removeLayer(this.pickupMarker);
      }
      if (this.destinationMarker) {
        this.map.removeLayer(this.destinationMarker);
      }

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

      this.pickupMarker = L.marker([pickupAddr.latitude, pickupAddr.longitude], { icon: pickupIcon })
        .addTo(this.map)
        .bindPopup(`<b>Pickup:</b><br>${pickupAddr.street || 'Unknown'}`);

      this.destinationMarker = L.marker([destAddr.latitude, destAddr.longitude], { icon: destIcon })
        .addTo(this.map)
        .bindPopup(`<b>Destination:</b><br>${destAddr.street || 'Unknown'}`);

      // Draw route line
      if (this.routeLine) {
        this.map.removeLayer(this.routeLine);
      }

      this.routeLine = L.polyline([
        [pickupAddr.latitude, pickupAddr.longitude],
        [destAddr.latitude, destAddr.longitude]
      ], { color: '#2ec4b6', weight: 4 }).addTo(this.map);

      // Fit bounds
      const bounds = L.latLngBounds([
        [pickupAddr.latitude, pickupAddr.longitude],
        [destAddr.latitude, destAddr.longitude]
      ]);
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }

  closePreview() {
    this.previewingRoute = null;
    this.clearMap();
  }
}
