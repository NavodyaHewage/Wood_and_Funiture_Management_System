import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../environment/environment';

// Response interfaces matching backend DTOs
export interface LoginRequest {
    username: string;
    password: string;
}

export interface SignupRequest {
    username?: string;
    email?: string;
    password?: string;
    role?: string;
    userDetails?: string;
    isSystemUser?: boolean;
    entityType?: string;
    fullName?: string;
    nic?: string;
    address?: string;
    mobile?: string;
    designation?: string;
    dateJoined?: string;
    supCat?: string;
}

export interface JwtResponse {
    token: string;
    refreshToken: string;
    type: string;
    userId: number;
    username: string;
    email: string;
    role: string;
    expiresIn: number;
    passwordResetRequired?: boolean;
}

export interface MessageResponse {
    message: string;
}

export interface CurrentUser {
    userId: number;
    username: string;
    email: string;
    role: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/auth`;
    private currentUserSubject: BehaviorSubject<any>;
    public currentUser: Observable<any>;

    constructor(
        private http: HttpClient,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {
        const storedUser = this.isBrowser() ? localStorage.getItem('currentUser') : null;
        this.currentUserSubject = new BehaviorSubject<any>(
            storedUser ? JSON.parse(storedUser) : null
        );
        this.currentUser = this.currentUserSubject.asObservable();
    }

    private isBrowser(): boolean {
        return isPlatformBrowser(this.platformId);
    }

    /**
     * Login user with username and password
     */
    login(username: string, password: string): Observable<JwtResponse> {
        const loginRequest: LoginRequest = { username, password };

        return this.http.post<JwtResponse>(`${this.apiUrl}/login`, loginRequest).pipe(
            tap(response => {
                const currentUser: CurrentUser = {
                    userId: response.userId,
                    username: response.username,
                    email: response.email,
                    role: this.normalizeRole(response.role)
                };

                // Store user data and tokens
                if (this.isBrowser()) {
                    localStorage.setItem('accessToken', response.token);
                    localStorage.setItem('refreshToken', response.refreshToken);
                    localStorage.setItem('currentUser', JSON.stringify(currentUser));
                }
                this.currentUserSubject.next(currentUser);
            })
        );
    }

    /**
     * Register new user
     */
    signup(signupData: SignupRequest): Observable<MessageResponse> {
        return this.http.post<MessageResponse>(`${this.apiUrl}/signup`, signupData);
    }

    /**
     * Logout current user
     */
    logout(): Observable<MessageResponse> {
        return this.http.post<MessageResponse>(`${this.apiUrl}/logout`, {}).pipe(
            tap(() => {
                this.clearLocalSession();
            }),
            catchError(err => {
                // Even if backend logout fails, clear local session
                this.clearLocalSession();
                throw err;
            })
        );
    }

    private clearLocalSession() {
        if (this.isBrowser()) {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('currentUser');
        }
        this.currentUserSubject.next(null);
    }

    /**
     * Refresh access token
     */
    refreshToken(): Observable<JwtResponse> {
        const refreshToken = this.isBrowser() ? localStorage.getItem('refreshToken') : null;

        return this.http.post<JwtResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
            tap(response => {
                if (this.isBrowser()) {
                    localStorage.setItem('accessToken', response.token);
                    localStorage.setItem('refreshToken', response.refreshToken);
                }
            })
        );
    }

    /**
     * Check if user is authenticated
     */
    isAuthenticated(): boolean {
        const token = this.isBrowser() ? localStorage.getItem('accessToken') : null;
        return !!token;
    }

    /**
     * Get current user value
     */
    get currentUserValue(): any {
        return this.currentUserSubject.value;
    }

    getDashboardRoute(role: string | null | undefined): string {
        const normalizedRole = this.normalizeRole(role);

        switch (normalizedRole) {
            case 'supplier':
                return '/supplier-dashboard';
            case 'employee':
                return '/employee-dashboard';
            case 'admin':
            default:
                return '/admin-dashboard';
        }
    }

    normalizeRole(role: string | null | undefined): string {
        return (role || '').toString().trim().toLowerCase().replace(/^role_/, '');
    }

    /**
     * Get access token
     */
    getToken(): string | null {
        return this.isBrowser() ? localStorage.getItem('accessToken') : null;
    }

    /**
     * Change password using username and old password, with email and phone verification (public access)
     */
    changePassword(username: string, oldPassword: string, newPassword: string, email: string, phoneNumber: string): Observable<MessageResponse> {
        return this.http.patch<MessageResponse>(`${environment.apiUrl}/users/change-password`, { username, oldPassword, newPassword, email, phoneNumber });
    }

    /**
     * Forgot password - reset to default 'password123' (public access)
     */
    forgotPassword(username: string): Observable<MessageResponse> {
        return this.http.post<MessageResponse>(`${this.apiUrl}/forgot-password`, { username });
    }
}
