import { Component, OnInit } from '@angular/core';
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
    private authService: AuthService
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
    this.loading = true;
    this.errorMessage = '';
    
    this.rideService.getDriverRideHistory(
      this.driverId, 
      this.startDate || undefined, 
      this.endDate || undefined
    ).subscribe({
      next: (rides) => {
        console.log('Driver ride history loaded:', rides);
        this.rides = rides;
        this.filteredRides = rides;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading driver ride history:', err);
        this.errorMessage = err?.error?.message || err?.message || 'Failed to load ride history';
        this.loading = false;
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
