import { Routes } from '@angular/router';
import { Login } from './components/auth/login/login';
import { Register } from './components/auth/register/register';
import { AdminDashComponent } from './components/user-management/admin-dash/admin-dash.component';
import { ChangePassword } from './components/auth/change-password/change-password';
import { UserManagementDashboardComponent } from './components/user-management/user-management-dashboard/user-management-dashboard.component';

import { ProfileComponent } from './components/profile/profile.component';
import { EmployeeManagementComponent } from './components/user-management/employee-management/employee-management.component';
import { SupplierManagementComponent } from './components/user-management/supplier-management/supplier-management.component';
import { CustomerManagementComponent } from './components/user-management/customer-management/customer-management.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login, data: { title: 'User Login' } },
    { path: 'register', component: Register, data: { title: 'Register New User' } },
    { path: 'admin-dashboard', component: AdminDashComponent, data: { title: 'Admin Dashboard' } },
    { path: 'user-management', component: UserManagementDashboardComponent, data: { title: 'User Management' } },
    { path: 'employee-management', component: EmployeeManagementComponent, data: { title: 'Employee Management' } },
    { path: 'supplier-management', component: SupplierManagementComponent, data: { title: 'Supplier Management' } },
    { path: 'customer-management', component: CustomerManagementComponent, data: { title: 'Customer Management' } },
    { path: 'change-password', component: ChangePassword, data: { title: 'Change Password' } },
    { path: 'profile', component: ProfileComponent, data: { title: 'User Profile' } }
];
