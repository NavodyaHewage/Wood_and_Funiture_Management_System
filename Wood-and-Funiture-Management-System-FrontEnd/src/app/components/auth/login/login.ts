import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';
import { SessionService } from '../../../service/session.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  loginData: any = {
    username: '',
    password: ''
  };

  isLoading = false;
  errorMessage = '';
  successMessage = '';
  rememberMe = false;
  remainingAttempts: number | null = null;
  lockCountdown = 0;
  countdownInterval: any;
  showPassword = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private toastService: ToastService,
    private sessionService: SessionService
  ) { }

  onLogin() {
    // ... validation logic ...
    this.authService.login(this.loginData.username, this.loginData.password).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.toastService.showSuccess('Login successful! Welcome back.');
        this.sessionService.startMonitoring();

        // Show success message briefly before redirecting
        setTimeout(() => {
          // Role-based routing
          const userRole = response.role ? response.role.toLowerCase() : '';
          if (userRole === 'supplier') {
            this.router.navigate(['/supplier-dashboard']);
          } else if (userRole === 'manager') {
            this.router.navigate(['/manager-dashboard']);
          } else {
            // Admin or fallback
            this.router.navigate(['/admin-dashboard']);
          }
        }, 800);
      },
      error: (error) => {
        this.isLoading = false;

        // Handle different error responses
        if (error.error && error.error.message) {
          const msg = error.error.message;
          this.errorMessage = msg;
          this.toastService.showError(msg);

          // Extract remaining attempts if present
          if (msg.includes('attempts remaining')) {
            const match = msg.match(/(\d+) attempts remaining/);
            if (match) {
              this.remainingAttempts = parseInt(match[1]);
            }
          } else {
            this.remainingAttempts = null;
          }

          // Check if locked
          if (msg.toLowerCase().includes('locked') || error.status === 423) {
            this.startLockCountdown(5 * 60); // 5 minutes
          }
        } else if (error.status === 401) {
          this.toastService.showError('Invalid username or password');
          this.remainingAttempts = null;
        } else if (error.status === 423) {
          this.toastService.showError('Your account has been locked. Please try again in 5 minutes.');
          this.startLockCountdown(5 * 60);
        } else if (error.status === 403) {
          this.toastService.showError('Your account has been disabled. Please contact support.');
          this.remainingAttempts = null;
        } else if (error.status === 0) {
          this.toastService.showError('Unable to connect to server. Please check your connection.');
          this.remainingAttempts = null;
        } else {
          this.toastService.showError('An error occurred during login. Please try again.');
          this.remainingAttempts = null;
        }

        console.error('Login error:', error);
      }
    });
  }

  startLockCountdown(seconds: number) {
    this.lockCountdown = seconds;
    if (this.countdownInterval) clearInterval(this.countdownInterval);

    this.countdownInterval = setInterval(() => {
      this.lockCountdown--;
      if (this.lockCountdown <= 0) {
        clearInterval(this.countdownInterval);
        this.errorMessage = '';
      }
    }, 1000);
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  ngOnDestroy() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }
}
