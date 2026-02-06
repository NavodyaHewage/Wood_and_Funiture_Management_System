import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';

@Component({
    selector: 'app-change-password',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './change-password.html',
    styleUrls: ['./change-password.css']
})
export class ChangePassword {
    passwordData = {
        username: '',
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    };

    isLoading = false;
    errorMessage = '';
    successMessage = '';

    showOldPassword = false;
    showNewPassword = false;
    showConfirmPassword = false;

    passwordStrength = 0;
    strengthLabel = '';
    strengthColor = 'transparent';

    constructor(
        private authService: AuthService,
        private router: Router,
        private toastService: ToastService
    ) { }

    checkPasswordStrength() {
        const password = this.passwordData.newPassword;
        if (!password) {
            this.passwordStrength = 0;
            this.strengthLabel = '';
            return;
        }

        let strength = 0;
        if (password.length >= 8) strength += 25;
        if (/[A-Z]/.test(password)) strength += 25;
        if (/[0-9]/.test(password)) strength += 25;
        if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength += 25;

        this.passwordStrength = strength;

        if (strength <= 25) {
            this.strengthLabel = 'Weak';
            this.strengthColor = '#ff4d4d';
        } else if (strength <= 50) {
            this.strengthLabel = 'Fair';
            this.strengthColor = '#ffa64d';
        } else if (strength <= 75) {
            this.strengthLabel = 'Good';
            this.strengthColor = '#a3cfbb';
        } else {
            this.strengthLabel = 'Strong';
            this.strengthColor = '#198754';
        }
    }

    onSubmit() {
        this.errorMessage = '';
        this.successMessage = '';

        if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
            this.toastService.showError('Passwords do not match');
            return;
        }

        if (this.passwordData.newPassword.length < 6) {
            this.toastService.showWarning('New password must be at least 6 characters');
            return;
        }

        this.isLoading = true;
        this.authService.changePassword(
            this.passwordData.username,
            this.passwordData.oldPassword,
            this.passwordData.newPassword
        ).subscribe({
            next: (response) => {
                this.isLoading = false;
                this.toastService.showSuccess('Password changed successfully! Please login with your new password.');
                this.passwordData = { username: '', oldPassword: '', newPassword: '', confirmPassword: '' };

                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 1500);
            },
            error: (error) => {
                this.isLoading = false;
                const errorMsg = error.error?.message || 'Failed to change password. Please verify your old password.';
                this.toastService.showError(errorMsg);
            }
        });
    }

    goBack() {
        window.history.back();
    }
}
