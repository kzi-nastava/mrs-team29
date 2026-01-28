import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FavoriteRouteService } from '../../services/favorite-route.service';
import { FavoriteRoute } from '../../models/favorite-route.model';

@Component({
  selector: 'app-favorite-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorite-popup.component.html',
  styleUrls: ['./favorite-popup.component.css']
})
export class FavoritePopupComponent {

  favorites: FavoriteRoute[] = [];
  @Output() close = new EventEmitter<void>();

  constructor(private favService: FavoriteRouteService) {
    this.favService
      .getMyFavorites('userId')
      .subscribe(f => this.favorites = f);
  }

  order(routeId: string) {
    this.favService.orderFromFavorite(routeId).subscribe();
  }
}
