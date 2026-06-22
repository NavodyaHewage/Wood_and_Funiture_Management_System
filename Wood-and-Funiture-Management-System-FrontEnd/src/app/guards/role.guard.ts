import { inject } from '@angular/core';
import { Router, CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../service/auth.service';

export const roleGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const expectedRole = route.data['requiredRole']?.toLowerCase();
  const currentUser = authService.currentUserValue;

  if (currentUser && currentUser.role) {
    const userRole = authService.normalizeRole(currentUser.role);
    if (userRole === expectedRole || !expectedRole) {
      return true;
    } else {
      // Role not authorized, redirect to their proper dashboard
      router.navigateByUrl(authService.getDashboardRoute(currentUser.role));
      return false;
    }
  }

  // Not logged in
  router.navigate(['/login']);
  return false;
};
