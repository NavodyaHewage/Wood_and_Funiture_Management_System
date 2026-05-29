import { inject } from '@angular/core';
import { Router, CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { PermissionService } from '../service/permission.service';
import { AuthService } from '../service/auth.service';

export const permissionGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const permissionService = inject(PermissionService);

  const currentUser = authService.currentUserValue;
  if (!currentUser) {
    router.navigate(['/login']);
    return false;
  }

  const userRole = authService.normalizeRole(currentUser.role);

  // Admin automatically bypasses all permission checks
  if (userRole === 'admin') {
    return true;
  }

  // If role is supplier, they should go to supplier dashboard/routes
  if (userRole === 'supplier') {
    const isSupplierRoute = state.url.startsWith('/supplier-dashboard') || state.url.startsWith('/supply-request-management') || state.url.startsWith('/my-supplies');
    if (isSupplierRoute) {
      return true;
    }
    router.navigateByUrl(authService.getDashboardRoute(currentUser.role));
    return false;
  }

  // Evaluate Employee dynamic permissions
  const requiredFunction = route.data['requiredFunction'];
  if (requiredFunction) {
    if (permissionService.hasPermission(requiredFunction)) {
      return true;
    } else {
      console.warn(`Access blocked to ${state.url} - Missing permission: ${requiredFunction}`);
      router.navigate(['/employee-dashboard']);
      return false;
    }
  }

  // Default fallback if no specific function is specified on route
  return true;
};
