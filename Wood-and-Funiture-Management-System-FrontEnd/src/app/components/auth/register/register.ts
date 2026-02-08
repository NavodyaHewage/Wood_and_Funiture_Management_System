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
    isSystemUser: true,
    entityType: 'EMPLOYEE',
    username: '',
    password: '',
    confirmPassword: '',
    role: 'ADMIN',
    email: '',
    mobile: '',
    address: '',
    nic: '',
    fullName: '',
    designation: '',
    dateJoined: '',
    supCat: 'Local'
  };

  isLoading = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private toastService: ToastService
  ) { }

  onUserTypeChange() {
    if (this.registerData.isSystemUser) {
      if (this.registerData.role === 'SUPPLIER') {
        this.registerData.supCat = 'Regular';
      }
    } else {
      if (this.registerData.entityType === 'SUPPLIER' && this.registerData.supCat === 'Regular') {
        this.registerData.supCat = 'Local';
      }
    }
  }

  onRoleChange() {
    if (this.registerData.isSystemUser && this.registerData.role === 'SUPPLIER') {
      this.registerData.supCat = 'Regular';
    }
  }

  onEntityTypeChange() {
    if (!this.registerData.isSystemUser && this.registerData.entityType === 'SUPPLIER') {
      if (this.registerData.supCat === 'Regular') {
        this.registerData.supCat = 'Local';
      }
    }
  }

  validateNIC(nic: string): boolean {
    const nicRegex = /^(\d{9}[vV]|\d{12})$/;
    return nicRegex.test(nic);
  }

  validateMobile(mobile: string): boolean {
    // Basic 10 digit validation
    const mobileRegex = /^\d{10}$/;
    return mobileRegex.test(mobile);
  }

  onRegister() {
    if (this.registerData.isSystemUser) {
      if (this.registerData.password !== this.registerData.confirmPassword) {
        this.toastService.showError('Passwords do not match!');
        return;
      }
      if (!this.registerData.username || !this.registerData.password) {
        this.toastService.showWarning('Please fill in username and password for system users.');
        return;
      }
    }

    if (this.registerData.isSystemUser && this.registerData.role === 'SUPPLIER' && this.registerData.supCat !== 'Regular') {
      this.toastService.showError('Only Regular suppliers can have system accounts!');
      return;
    }

    if (!this.registerData.fullName || !this.registerData.mobile) {
      this.toastService.showWarning('Please fill in Name and Mobile Number.');
      return;
    }

    if (!this.validateMobile(this.registerData.mobile)) {
      this.toastService.showError('Invalid Mobile Number! Please enter 10 digits.');
      return;
    }

    // Validate NIC
    if (this.registerData.nic) {
      if (!this.validateNIC(this.registerData.nic)) {
        this.toastService.showError('Invalid NIC format! Use 9 numbers + V or 12 numbers.');
        return;
      }
    } else {
      this.toastService.showWarning('NIC Number is required!');
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
