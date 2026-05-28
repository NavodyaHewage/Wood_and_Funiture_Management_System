import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { AuthService } from './auth.service';

export interface UserPermissionDto {
  functionName: string;
  canAccess: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private apiUrl = `${environment.apiUrl}/permissions`;
  
  // BehaviorSubject to cache and expose current user's permissions
  private myPermissionsSubject = new BehaviorSubject<string[]>([]);
  public myPermissions$ = this.myPermissionsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    // Try to load cached permissions from localStorage on startup
    if (this.isBrowser()) {
      const cached = localStorage.getItem('userPermissions');
      if (cached) {
        this.myPermissionsSubject.next(JSON.parse(cached));
      }
    }

    // Subscribe to auth service to clear or fetch permissions on user change
    this.authService.currentUser.subscribe(user => {
      if (user) {
        this.loadMyPermissions().subscribe();
      } else {
        this.clearPermissions();
      }
    });
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  /**
   * Fetch permission checklist for a user (Admin only)
   */
  getUserPermissions(userId: number): Observable<UserPermissionDto[]> {
    return this.http.get<UserPermissionDto[]>(`${this.apiUrl}/user/${userId}`);
  }

  /**
   * Save permission checklist for a user (Admin only)
   */
  saveUserPermissions(userId: number, permissions: UserPermissionDto[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/${userId}`, permissions);
  }

  /**
   * Fetch allowed functions for current logged-in user
   */
  loadMyPermissions(): Observable<string[]> {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      return of([]);
    }

    // Admins automatically get all permissions in frontend
    if (this.authService.normalizeRole(currentUser.role) === 'admin') {
      const allFuncs = [
        'employee-management',
        'attendance-management',
        'loan-management',
        'payroll-management',
        'designation-salary',
        'supplier-management',
        'supply-request-management',
        'log-management',
        'raw-material-cutting',
        'customer-management',
        'quotation-management',
        'order-management',
        'receipts',
        'expenses',
        'product-category'
      ];
      this.myPermissionsSubject.next(allFuncs);
      if (this.isBrowser()) {
        localStorage.setItem('userPermissions', JSON.stringify(allFuncs));
      }
      return of(allFuncs);
    }

    return this.http.get<string[]>(`${this.apiUrl}/me`).pipe(
      tap(perms => {
        const lowerPerms = perms.map(p => p.toLowerCase());
        this.myPermissionsSubject.next(lowerPerms);
        if (this.isBrowser()) {
          localStorage.setItem('userPermissions', JSON.stringify(lowerPerms));
        }
      }),
      catchError(err => {
        console.error('Error loading permissions:', err);
        return of([]);
      })
    );
  }

  /**
   * Check if user is authorized for a specific function
   */
  hasPermission(functionName: string): boolean {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return false;
    
    // Admin always has permission
    const userRole = this.authService.normalizeRole(currentUser.role);

    if (userRole === 'admin') return true;

    // Supplier is only checked by roleGuard, but let's allow supply-requests
    if (userRole === 'supplier') {
      return functionName === 'supply-request-management';
    }

    // Manager permissions check
    const myPerms = this.myPermissionsSubject.value;
    return myPerms.includes(functionName.toLowerCase());
  }

  /**
   * Clear cached permissions on logout
   */
  clearPermissions() {
    if (this.isBrowser()) {
      localStorage.removeItem('userPermissions');
    }
    this.myPermissionsSubject.next([]);
  }
}
