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
    private timerSubscription?: Subscription;

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
        this.updateActivity(false); // Initial capture without refresh

        // Activities that reset the timer
        const activity$ = merge(
            fromEvent(window, 'mousemove'),
            fromEvent(window, 'mousedown'),
            fromEvent(window, 'keydown'), // Changed from keypress for better coverage
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

            // More precise check using a dedicated timer
            this.startScheduleCheck();
        });
    }

    private startScheduleCheck() {
        if (this.timerSubscription) {
            this.timerSubscription.unsubscribe();
        }

        // Check every 10 seconds for higher precision
        this.timerSubscription = timer(10000, 10000).subscribe(() => {
            this.checkIdleTime();
        });
    }

    /**
     * Update the last activity timestamp
     */
    private updateActivity(shouldRefresh: boolean = true) {
        if (!this.isBrowser() || !this.authService.isAuthenticated()) return;

        const now = Date.now();
        const last = Number(localStorage.getItem(this.LAST_ACTIVITY_KEY) || 0);

        localStorage.setItem(this.LAST_ACTIVITY_KEY, now.toString());

        // Proactive Refresh: If we've been active and more than 2 minutes passed since last update
        // We only refresh if shouldRefresh is true (avoid infinite loops/heavy traffic)
        if (shouldRefresh && last > 0 && now - last > 120000 && now - last < this.TIMEOUT_MS) {
            this.ngZone.run(() => {
                this.authService.refreshToken().subscribe({
                    error: () => this.handleTimeout()
                });
            });
        }
    }

    private checkIdleTime() {
        if (!this.isBrowser() || !this.authService.isAuthenticated()) return;

        const lastActivity = Number(localStorage.getItem(this.LAST_ACTIVITY_KEY) || 0);
        const now = Date.now();

        if (lastActivity === 0) return;

        if (now - lastActivity >= this.TIMEOUT_MS) {
            this.ngZone.run(() => {
                console.log('Inactivity limit reached. Logging out...');
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
            this.inactivitySubscription = undefined;
        }
        if (this.timerSubscription) {
            this.timerSubscription.unsubscribe();
            this.timerSubscription = undefined;
        }
    }

    private handleTimeout() {
        if (this.authService.isAuthenticated()) {
            this.stopMonitoring();
            localStorage.removeItem(this.LAST_ACTIVITY_KEY);
            this.toastService.showWarning('Session expired due to 5 minutes of inactivity.');

            this.authService.logout().subscribe({
                next: () => {
                    this.router.navigate(['/login']);
                },
                error: () => {
                    // Fallback if backend call fails (already handled in interceptor but safe to have here)
                    this.router.navigate(['/login']);
                }
            });
        }
    }
}
