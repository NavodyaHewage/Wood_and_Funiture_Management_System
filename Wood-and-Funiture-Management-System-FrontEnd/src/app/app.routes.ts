import { Routes } from '@angular/router';
import { roleGuard } from './guards/role.guard';
import { Login } from './components/auth/login/login';
import { Register } from './components/auth/register/register';
import { AdminDashComponent } from './components/user-management/admin-dash/admin-dash.component';
import { ChangePassword } from './components/auth/change-password/change-password';
import { UserManagementDashboardComponent } from './components/user-management/user-management-dashboard/user-management-dashboard.component';

import { ProfileComponent } from './components/profile/profile.component';
import { EmployeeManagementComponent } from './components/user-management/employee-management/employee-management.component';
import { SupplierManagementComponent } from './components/user-management/supplier-management/supplier-management.component';
import { CustomerManagementComponent } from './components/user-management/customer-management/customer-management.component';
import { SuppliyerManagementDashboardComponent } from './components/user-management/suppliyer-management-dashboard/suppliyer-management-dashboard.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login, data: { title: 'User Login' } },
    { path: 'register', component: Register, data: { title: 'Register New User' } },
    { path: 'admin-dashboard', component: AdminDashComponent, canActivate: [roleGuard], data: { title: 'Admin Dashboard', requiredRole: 'Admin' } },
    { path: 'user-management', component: UserManagementDashboardComponent, canActivate: [roleGuard], data: { title: 'User Management', requiredRole: 'Admin' } },
    { path: 'employee-management', component: EmployeeManagementComponent, canActivate: [roleGuard], data: { title: 'Employee Management', requiredRole: 'Admin' } },
    { path: 'supplier-management', component: SupplierManagementComponent, canActivate: [roleGuard], data: { title: 'Supplier Management', requiredRole: 'Admin' } },
    { path: 'customer-management', component: CustomerManagementComponent, canActivate: [roleGuard], data: { title: 'Customer Management', requiredRole: 'Admin' } },
    { path: 'change-password', component: ChangePassword, canActivate: [roleGuard], data: { title: 'Change Password' } },
    { path: 'profile', component: ProfileComponent, canActivate: [roleGuard], data: { title: 'User Profile' } },
    { path: 'supplier-dashboard', component: SuppliyerManagementDashboardComponent, canActivate: [roleGuard], data: { title: 'Supplier Dashboard', requiredRole: 'Supplier' } }
];
