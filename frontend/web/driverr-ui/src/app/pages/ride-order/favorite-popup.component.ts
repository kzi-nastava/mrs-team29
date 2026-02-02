import { Component, Output, EventEmitter, OnInit } from '@angular/core';
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
export class FavoritePopupComponent implements OnInit {

  favorites: FavoriteRoute[] = [];
  userId = 'USER_ID_123'; // later from auth
  @Output() close = new EventEmitter<void>();

  constructor(private favService: FavoriteRouteService) {}

  ngOnInit() {
    this.favService.getMyFavorites(this.userId).subscribe(f => this.favorites = f);
  }

  order(routeId: string) {
    this.favService.orderFromFavorite(routeId, this.userId).subscribe();
  }
}

