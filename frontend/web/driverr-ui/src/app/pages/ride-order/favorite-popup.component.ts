import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FavoriteRouteService } from '../../services/favorite-route.service';
import { FavoriteRoute } from '../../models/favorite-route.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-favorite-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorite-popup.component.html',
  styleUrls: ['./favorite-popup.component.css']
})
export class FavoritePopupComponent implements OnInit {

  favorites: FavoriteRoute[] = [];
  userId = '';
  @Output() close = new EventEmitter<void>();

  constructor(
    private favService: FavoriteRouteService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.userId = this.authService.getUserId();
    if (!this.userId) {
      return;
    }
    this.favService.getMyFavorites(this.userId).subscribe(f => this.favorites = f);
  }

  order(routeId: string) {
    if (!this.userId) {
      return;
    }
    this.favService.orderFromFavorite(routeId, this.userId).subscribe();
  }
}

