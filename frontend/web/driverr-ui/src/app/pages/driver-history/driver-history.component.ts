import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RideService } from '../../services/ride.service';
import { AuthService } from '../../services/auth.service';
import { Ride } from '../../models/ride.model';

@Component({
  selector: 'app-driver-history',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './driver-history.component.html',
  styleUrls: ['./driver-history.component.css'],
})
export class DriverHistoryComponent implements OnInit {
  rides: Ride[] = [];
  filteredRides: Ride[] = [];
  loading = false;
  errorMessage = '';
  driverId = '';
  
  startDate = '';
  endDate = '';

  constructor(
    private rideService: RideService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.driverId = this.authService.getUserId();
    if (!this.driverId) {
      this.errorMessage = 'Please log in as a driver.';
      return;
    }
    this.loadRideHistory();
  }

  loadRideHistory() {
    if (this.loading) {
      return;
    }

    if (this.startDate && this.endDate && this.startDate > this.endDate) {
      this.errorMessage = 'Start date cannot be after end date.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    
    this.rideService.getDriverRideHistory(
      this.driverId, 
      this.startDate || undefined, 
      this.endDate || undefined
    ).subscribe({
      next: (rides) => {
        console.log('Driver ride history loaded:', rides);
        this.rides = Array.isArray(rides) ? [...rides] : [];
        this.filteredRides = [...this.rides];
        this.errorMessage = '';
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading driver ride history:', err);
        this.errorMessage = (typeof err?.error === 'string' && err.error)
          || err?.error?.message
          || err?.message
          || 'Failed to load ride history';
        this.rides = [];
        this.filteredRides = [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyDateFilter() {
    this.loadRideHistory();
  }

  clearDateFilter() {
    this.startDate = '';
    this.endDate = '';
    this.loadRideHistory();
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-GB', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
      });
    } catch (e) {
      return dateString;
    }
  }

  formatPrice(price: number): string {
    return `RSD ${price.toFixed(2)}`;
  }

  getTotalEarnings(): number {
    return this.filteredRides.reduce((sum, ride) => sum + ride.price, 0);
  }
}
