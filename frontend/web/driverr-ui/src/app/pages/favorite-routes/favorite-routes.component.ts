import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FavoriteRouteService } from '../../services/favorite-route.service';

@Component({
  selector: 'app-favorite-routes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './favorite-routes.component.html',
  styleUrls: ['./favorite-routes.component.css']
})
export class FavoriteRoutesComponent implements OnInit {

  favoriteRoutes: any[] = [];
  form!: FormGroup;
  editingId: string | null = null;
  showForm = false;
  message = '';
  errorMessage = '';
  loading = false;
  userId = 'USER_ID_123'; // later from auth

  constructor(
    private fb: FormBuilder,
    private favoriteRouteService: FavoriteRouteService
  ) {}

  ngOnInit() {
    this.initializeForm();
    this.loadFavoriteRoutes();
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
  }
}
