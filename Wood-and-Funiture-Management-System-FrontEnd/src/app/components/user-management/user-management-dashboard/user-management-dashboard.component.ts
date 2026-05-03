import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserManagementService, User } from '../../../service/user-management.service';
import { ToastService } from '../../../service/toast.service';
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

  constructor(
    private userService: UserManagementService,
    private toastService: ToastService,
    private router: Router
  ) { }

  ngOnInit(): void {
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

  onToggleStatus(user: User) {
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

  onDelete(id: number, username: string) {
    if (confirm(`Are you sure you want to delete user "${username}"? This action cannot be undone.`)) {
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
  }

  onAddUser() {
    this.router.navigate(['/register']);
  }

  onUpdateRole(user: User, newRole: string) {
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
}
