import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { AdminDriverRegisterComponent } from './pages/register/driverRegister.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { DriverHistoryComponent } from './pages/driver-history/driver-history.component';
import { DriverRideComponent } from './pages/driver-ride/driver-ride.component';
import { OrderRideComponent } from './pages/ride-order/ride-order.component';
import { DriverActivationComponent } from './pages/driver-activation/driver-activation.component';
import { AdminApprovalComponent } from './pages/admin-approval/admin-approval.component';
import { UserManagementComponent } from './pages/user-management/user-management.component';
import { FavoriteRoutesComponent } from './pages/favorite-routes/favorite-routes.component';
import { MainPageComponent } from './pages/main-page/main-page.component';
import { ActivateComponent } from './pages/activate/activate.component';
import { RequestPasswordResetComponent } from './pages/request-password-reset/request-password-reset.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { authGuard } from './guards/auth.guard.js';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'activate', component: ActivateComponent },
  { path: 'forgot-password', component: RequestPasswordResetComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'driver-activate', component: DriverActivationComponent }, // 2.2.3
  { path: 'admin/driver-register', component: AdminDriverRegisterComponent, canActivate: [authGuard] },
  { path: 'admin/approvals', component: AdminApprovalComponent, canActivate: [authGuard] }, // 2.3 Admin dashboard
  { path: 'admin/users', component: UserManagementComponent, canActivate: [authGuard] }, // User blocking management
  { path: 'main-page', component: MainPageComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] }, // 2.3 with password change
  { path: 'driver-history', component: DriverHistoryComponent, canActivate: [authGuard] },
  { path: 'driver-ride', component: DriverRideComponent, canActivate: [authGuard] }, // 2.6.1 Driver current ride
  { path: 'order-ride', component: OrderRideComponent, canActivate: [authGuard] }, // 2.4.1 Enhanced
  { path: 'favorite-routes', component: FavoriteRoutesComponent, canActivate: [authGuard] }, // 2.4.3
  { path: 'driver/history', component: DriverHistoryComponent, canActivate: [authGuard] },
  { path: 'favorites', redirectTo: 'favorite-routes' },
];