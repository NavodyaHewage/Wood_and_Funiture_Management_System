import { Injectable, NgZone, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { ToastService } from './toast.service';
import { fromEvent, merge, Subscription, timer } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class SessionService {
    private readonly TIMEOUT_MS = 300000; // 5 minutes
    private readonly LAST_ACTIVITY_KEY = 'last_activity';
    private inactivitySubscription?: Subscription;
    private watchdogTimer?: any;

    constructor(
        private authService: AuthService,
        private router: Router,
        private toastService: ToastService,
        private ngZone: NgZone,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    private isBrowser(): boolean {
        return isPlatformBrowser(this.platformId);
    }

    /**
     * Start monitoring user activity
     */
    startMonitoring() {
        if (!this.isBrowser()) return;

        this.stopMonitoring();
        this.updateActivity(); // Initial activity capture

        // Activities that reset the timer
        const activity$ = merge(
            fromEvent(window, 'mousemove'),
            fromEvent(window, 'mousedown'),
            fromEvent(window, 'keypress'),
            fromEvent(window, 'touchstart'),
            fromEvent(window, 'scroll'),
            fromEvent(window, 'click')
        );

        this.ngZone.runOutsideAngular(() => {
            this.inactivitySubscription = activity$
                .pipe(
                    tap(() => this.updateActivity())
                )
                .subscribe();

            // Intermittent check to see if we reached the timeout (Watchdog)
            this.watchdogTimer = setInterval(() => {
                this.checkIdleTime();
            }, 30000); // Check every 30 seconds
        });

        // Immediate check on start (e.g. after refresh)
        this.checkIdleTime();
    }

    /**
     * Update the last activity timestamp
     */
    private updateActivity() {
        if (!this.isBrowser() || !this.authService.isAuthenticated()) return;

        const now = Date.now();
        const last = Number(localStorage.getItem(this.LAST_ACTIVITY_KEY) || 0);

        localStorage.setItem(this.LAST_ACTIVITY_KEY, now.toString());

        // Proactive Refresh: If we've been active and more than 2 minutes passed since last update
        // (but less than 5), refresh the token to slide the backend window.
        if (now - last > 120000 && now - last < this.TIMEOUT_MS) {
            this.ngZone.run(() => {
                this.authService.refreshToken().subscribe({
                    error: () => this.handleTimeout() // If refresh fails, session is likely dead
                });
            });
        }
    }

    private checkIdleTime() {
        if (!this.isBrowser() || !this.authService.isAuthenticated()) return;

        const lastActivity = Number(localStorage.getItem(this.LAST_ACTIVITY_KEY) || 0);
        const now = Date.now();

        if (now - lastActivity >= this.TIMEOUT_MS) {
            this.ngZone.run(() => {
                this.handleTimeout();
            });
        }
    }

    /**
     * Stop monitoring activity
     */
    stopMonitoring() {
        if (this.inactivitySubscription) {
            this.inactivitySubscription.unsubscribe();
        }
        if (this.watchdogTimer) {
            clearInterval(this.watchdogTimer);
        }
    }

    private handleTimeout() {
        if (this.authService.isAuthenticated()) {
            this.stopMonitoring();
            localStorage.removeItem(this.LAST_ACTIVITY_KEY);
            this.toastService.showWarning('Session expired due to 5 minutes of inactivity.');
            this.authService.logout().subscribe({
                complete: () => {
                    this.router.navigate(['/login']);
                    this.startMonitoring(); // Re-arm for next potential login
                }
            });
        }
    }
}
