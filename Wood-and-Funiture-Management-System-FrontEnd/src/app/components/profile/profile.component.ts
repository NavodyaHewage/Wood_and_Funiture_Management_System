import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserManagementService } from '../../service/user-management.service';
import { ToastService } from '../../service/toast.service';
import { HeaderComponent } from '../header/header.component';
import { AdminSideComponent } from '../user-management/admin-side/admin-side.component';

import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule, HeaderComponent, AdminSideComponent, TranslatePipe],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    user: any = null;
    isLoading = true;
    isEditing = false;

    // Edited data
    editData = {
        email: '',
        phoneNumber: '',
        userDetails: ''
    };

    constructor(
        private userService: UserManagementService,
        private toastService: ToastService
    ) { }

    ngOnInit(): void {
        this.loadUserProfile();
    }

    loadUserProfile() {
        this.isLoading = true;
        this.userService.getCurrentUser().subscribe({
            next: (userData) => {
                this.user = userData;
                this.editData = {
                    email: userData.email,
                    phoneNumber: userData.phoneNumber || '',
                    userDetails: userData.userDetails || ''
                };
                this.isLoading = false;
            },
            error: (err) => {
                this.isLoading = false;
                this.toastService.showError('Failed to load profile data.');
            }
        });
    }

    toggleEdit() {
        this.isEditing = !this.isEditing;
        if (!this.isEditing) {
            // Reset if cancelled
            this.editData = {
                email: this.user.email,
                phoneNumber: this.user.phoneNumber || '',
                userDetails: this.user.userDetails || ''
            };
        }
    }

    onUpdateProfile() {
        this.isLoading = true;
        this.userService.updateUser(this.user.userId, this.editData).subscribe({
            next: (res) => {
                this.toastService.showSuccess('Profile updated successfully!');
                this.isEditing = false;
                this.loadUserProfile();
            },
            error: (err) => {
                this.isLoading = false;
                const errMsg = err.error?.message || 'Failed to update profile.';
                this.toastService.showError(errMsg);
            }
        });
    }
}
