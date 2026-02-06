import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';

import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, HeaderComponent, AdminSideComponent],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register {
  registerData = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'EMPLOYEE'
  };

  isLoading = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private toastService: ToastService
  ) { }

  onRegister() {
    if (this.registerData.password !== this.registerData.confirmPassword) {
      this.toastService.showError('Passwords do not match!');
      return;
    }

    if (!this.registerData.username || !this.registerData.email || !this.registerData.password) {
      this.toastService.showWarning('Please fill in all required fields.');
      return;
    }

    this.isLoading = true;
    this.authService.signup(this.registerData).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.toastService.showSuccess('User registered successfully!');
        // Redirect back to user management dashboard after success
        setTimeout(() => {
          this.router.navigate(['/user-management']);
        }, 1500);
      },
      error: (err) => {
        this.isLoading = false;
        const errMsg = err.error?.message || 'Registration failed. Please check your data.';
        this.toastService.showError(errMsg);
      }
    });
  }
}
