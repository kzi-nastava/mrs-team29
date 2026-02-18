import { Component, OnInit, AfterViewInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideService } from '../../services/ride.service';
import { AuthService } from '../../services/auth.service';
import { Ride, RideStatus } from '../../models/ride.model';
import * as L from 'leaflet';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-driver-ride',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-ride.component.html',
  styleUrls: ['./driver-ride.component.css']
})
export class DriverRideComponent implements OnInit, AfterViewInit, OnDestroy {
  currentRide?: Ride;
  loading = true;
  errorMessage = '';
  successMessage = '';
  driverId = '';
  
  map!: L.Map;
  pickupMarker?: L.Marker;
  destinationMarker?: L.Marker;
  
  RideStatus = RideStatus;
  
  private pollSubscription?: Subscription;
  private actionInProgress = false;

  constructor(
    private rideService: RideService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.driverId = this.authService.getUserId();
    console.log('[DriverRide] Component initialized. driverId:', this.driverId);
    
    if (!this.driverId) {
      this.errorMessage = 'Please log in as a driver.';
      this.loading = false;
      console.error('[DriverRide] No driverId found');
      return;
    }
    
    this.loadCurrentRide();
    
    // Poll for updates every 10 seconds
    this.pollSubscription = interval(10000).subscribe(() => {
      if (this.actionInProgress) {
        return;
      }
      console.log('[DriverRide] Polling for ride updates');
      this.loadCurrentRide(true);
    });
  }

  ngAfterViewInit() {
    // Map will be initialized after data loads in loadCurrentRide
    // This prevents "Map container not found" error
  }

  ngOnDestroy() {
    if (this.pollSubscription) {
      this.pollSubscription.unsubscribe();
    }
  }

  initializeMap() {
    // Guard: check if element exists
    const mapElement = document.getElementById('driver-map');
    if (!mapElement) {
      console.error('Map container element not found');
      return;
    }

    try {
      // Default center: Novi Sad, Serbia
      this.map = L.map('driver-map').setView([45.2671, 19.8335], 13);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
      }).addTo(this.map);
    } catch (error) {
      console.error('Error initializing map:', error);
    }
  }

  loadCurrentRide(silent = false) {
    if (!silent) {
      this.loading = true;
      console.log('[DriverRide] Loading ride for driverId:', this.driverId, 'silent:', silent);
    }
    
    this.rideService.getDriverCurrentRide(this.driverId).subscribe({
      next: (ride) => {
        console.log('[DriverRide] Ride loaded successfully:', ride);
        this.currentRide = ride;
        this.errorMessage = '';
        this.loading = false;
        this.cdr.markForCheck();
        
        // Initialize map after data loads and template has been rendered
        setTimeout(() => {
          if (!this.map && document.getElementById('driver-map')) {
            console.log('[DriverRide] Initializing map after ride load');
            this.initializeMap();
            if (this.map) {
              this.updateMapMarkers();
            }
          }
        }, 100);
      },
      error: (err) => {
        console.error('[DriverRide] Error loading ride:', err);
        if (!silent) {
          this.currentRide = undefined;
          this.loading = false;
          this.errorMessage = err?.error?.message || err?.message || 'Failed to load current ride';
          this.cdr.markForCheck();
        }
      }
    });
  }

  updateMapMarkers() {
    // Clear existing markers
    if (this.pickupMarker) {
      this.map.removeLayer(this.pickupMarker);
    }
    if (this.destinationMarker) {
      this.map.removeLayer(this.destinationMarker);
    }

    if (!this.currentRide) return;

    // Note: We'd need lat/lng from the backend to properly place markers
    // For now, we'll just display the addresses in the info panel
    // In a real implementation, you'd parse coordinates from the address
  }

  startRide(event?: Event) {
    event?.preventDefault();
    event?.stopPropagation();

    if (!this.currentRide) return;

    this.actionInProgress = true;
    this.loading = true;
    this.errorMessage = '';
    
    this.rideService.startRide(this.currentRide.rideId, this.driverId).subscribe({
      next: (updatedRide) => {
        this.currentRide = updatedRide;
        this.successMessage = 'Ride started! Drive safely.';
        this.loading = false;
        this.actionInProgress = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('[DriverRide] startRide error:', err);
        this.errorMessage = (typeof err?.error === 'string' && err.error)
          || err?.error?.message
          || err?.error?.details
          || err?.error?.error
          || err?.message
          || 'Failed to start ride. Please try again.';
        this.loading = false;
        this.actionInProgress = false;
      }
    });
  }

  finishRide(event?: Event) {
    event?.preventDefault();
    event?.stopPropagation();

    if (!this.currentRide) return;

    this.actionInProgress = true;
    this.loading = true;
    this.errorMessage = '';
    
    this.rideService.finishRide(this.currentRide.rideId, this.driverId).subscribe({
      next: (updatedRide) => {
        this.successMessage = 'Ride completed! Payment has been processed.';
        this.currentRide = undefined;
        this.loading = false;
        this.actionInProgress = false;
        setTimeout(() => {
          this.successMessage = '';
          this.loadCurrentRide();
        }, 3000);
      },
      error: (err) => {
        console.error('[DriverRide] finishRide error:', err);
        this.errorMessage = (typeof err?.error === 'string' && err.error)
          || err?.error?.message
          || err?.error?.details
          || err?.error?.error
          || err?.message
          || 'Failed to finish ride. Please try again.';
        this.loading = false;
        this.actionInProgress = false;
      }
    });
  }

  canStartRide(): boolean {
    return this.currentRide?.status === RideStatus.ASSIGNED;
  }

  canFinishRide(): boolean {
    return this.currentRide?.status === RideStatus.IN_PROGRESS;
  }

  getStatusClass(status: RideStatus): string {
    switch (status) {
      case RideStatus.ASSIGNED:
        return 'status-assigned';
      case RideStatus.IN_PROGRESS:
        return 'status-in-progress';
      default:
        return 'status-default';
    }
  }

  getStatusText(status: RideStatus): string {
    switch (status) {
      case RideStatus.ASSIGNED:
        return 'Assigned - Ready to Start';
      case RideStatus.IN_PROGRESS:
        return 'In Progress';
      case RideStatus.FINISHED:
        return 'Finished';
      default:
        return status;
    }
  }

  formatPrice(price: number): string {
    return `RSD ${price.toFixed(2)}`;
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  }
}
