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
  showResetModal = false;

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

        // Check if password reset is required
        if (response.passwordResetRequired) {
          this.toastService.showWarning('Your password has been reset. Please change it to continue.');
          setTimeout(() => {
            this.router.navigate(['/change-password'], { queryParams: { username: this.loginData.username } });
          }, 1500);
          return;
        }

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

  onForgotPassword() {
    if (!this.loginData.username) {
      this.toastService.showWarning('Please enter your username first to reset your password.');
      return;
    }
    this.showResetModal = true;
  }

  cancelReset() {
    this.showResetModal = false;
  }

  confirmReset() {
    this.showResetModal = false;
    this.isLoading = true;
    
    this.authService.forgotPassword(this.loginData.username).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.successMessage = response.message;
        this.toastService.showSuccess(response.message);
        // Auto-fill password with default to help user
        this.loginData.password = 'password123';
      },
      error: (error) => {
        this.isLoading = false;
        const errorMsg = error.error?.message || 'Failed to reset password. Please check the username.';
        this.toastService.showError(errorMsg);
        this.errorMessage = errorMsg;
      }
    });
  }
}
