import { Component } from '@angular/core';
import { RideService } from '../../services/ride.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-order-ride',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ride-order.component.html',
  styleUrls: ['./ride-order.component.css']
})
export class OrderRideComponent {

  creatorId = 'USER_ID_123'; // privremeno

  pickup = '';
  destination = '';
  stops: string[] = [];

  pets = false;
  baby = false;

  passengerEmails = '';

  message = '';

  constructor(private rideService: RideService) {}

  addStop() {
    this.stops.push('');
  }

  orderRide() {
    const dto = {
      creatorId: this.creatorId,
      pickupAddress: { street: this.pickup },
      destinationAddress: { street: this.destination },
      stops: this.stops.map(s => ({ street: s })),
      passengerIds: [],
      pets: this.pets,
      baby: this.baby
    };

    this.rideService.orderRide(dto).subscribe({
      next: res => this.message = `Ride ordered! Price: ${res.price}`,
      error: err => this.message = err.error.message
    });
  }
}
