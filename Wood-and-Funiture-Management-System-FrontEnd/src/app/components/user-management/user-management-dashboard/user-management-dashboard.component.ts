import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserManagementService, User } from '../../../service/user-management.service';
import { ToastService } from '../../../service/toast.service';
import { AuthService } from '../../../service/auth.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../admin-side/admin-side.component';

@Component({
  selector: 'app-user-management-dashboard',
  standalone: true,
  imports: [CommonModule, HeaderComponent, AdminSideComponent],
  templateUrl: './user-management-dashboard.component.html',
  styleUrl: './user-management-dashboard.component.css'
})
export class UserManagementDashboardComponent implements OnInit {
  users: User[] = [];
  isLoading = false;
  currentUser: any = null;

  // Confirmation Modal State
  showConfirmModal = false;
  modalTitle = '';
  modalMessage = '';
  pendingAction: (() => void) | null = null;

  constructor(
    private userService: UserManagementService,
    private toastService: ToastService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.toastService.showError('Failed to load users');
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  getStatusCount(isActive: boolean): number {
    return this.users.filter(u => u.isActive === isActive).length;
  }

  onToggleStatus(user: User) {
    if (user.userId === this.currentUser?.userId) {
      this.toastService.showWarning('You cannot deactivate your own account.');
      return;
    }

    if (user.role === 'ADMIN' && user.isActive) {
      this.toastService.showWarning('Admin accounts cannot be deactivated to ensure system stability.');
      return;
    }

    const action = user.isActive ? 'deactivate' : 'activate';
    this.openConfirmModal(
      'Confirm Status Change',
      `Are you sure you want to ${action} user "${user.username}"?`,
      () => {
        this.userService.toggleUserStatus(user.userId).subscribe({
          next: (res) => {
            user.isActive = !user.isActive;
            this.toastService.showSuccess(`User ${user.username} ${user.isActive ? 'activated' : 'deactivated'} successfully`);
          },
          error: (err) => {
            this.toastService.showError('Failed to update user status');
          }
        });
      }
    );
  }

  onDelete(id: number, username: string) {
    if (id === this.currentUser?.userId) {
      this.toastService.showError('You cannot delete your own account.');
      return;
    }

    this.openConfirmModal(
      'Confirm Deletion',
      `Are you sure you want to delete user "${username}"? This action cannot be undone.`,
      () => {
        this.userService.deleteUser(id).subscribe({
          next: () => {
            this.users = this.users.filter(u => u.userId !== id);
            this.toastService.showSuccess(`User ${username} deleted successfully`);
          },
          error: (err) => {
            this.toastService.showError('Failed to delete user');
          }
        });
      }
    );
  }

  onAddUser() {
    this.router.navigate(['/register']);
  }

  onUpdateRole(user: User, newRole: string) {
    if (user.role === 'SUPPLIER') {
      this.toastService.showWarning('Supplier roles cannot be changed.');
      return;
    }

    if (user.role === 'ADMIN' && newRole !== 'ADMIN') {
      this.toastService.showWarning('Admin roles can only be changed to Admin.');
      return;
    }

    this.openConfirmModal(
      'Confirm Role Change',
      `Are you sure you want to change the role of "${user.username}" to "${newRole}"?`,
      () => {
        this.userService.updateUser(user.userId, { role: newRole }).subscribe({
          next: () => {
            user.role = newRole;
            this.toastService.showSuccess(`Role updated to ${newRole} for ${user.username}`);
          },
          error: (err) => {
            this.toastService.showError('Failed to update role');
          }
        });
      }
    );
  }

  // Modal helper methods
  openConfirmModal(title: string, message: string, callback: () => void) {
    this.modalTitle = title;
    this.modalMessage = message;
    this.pendingAction = callback;
    this.showConfirmModal = true;
  }

  closeModal() {
    this.showConfirmModal = false;
    this.pendingAction = null;
  }

  confirmAction() {
    if (this.pendingAction) {
      this.pendingAction();
    }
    this.closeModal();
  }
}
