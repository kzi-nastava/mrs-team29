import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { AdminDriverRegisterComponent } from './pages/register/driverRegister.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { DriverHistoryComponent } from './pages/driver-history/driver-history.component';
import { OrderRideComponent } from './pages/ride-order/ride-order.component';
import { authGuard } from './guards/auth.guard.js';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'admin/driver-register', component: AdminDriverRegisterComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'driver-history', component: DriverHistoryComponent, canActivate: [authGuard] },
  { path: 'order-ride', component: OrderRideComponent, canActivate: [authGuard] },
  { path: 'driver/history', component: DriverHistoryComponent, canActivate: [authGuard] },
  { path: 'favorites', redirectTo: 'order-ride' },
];