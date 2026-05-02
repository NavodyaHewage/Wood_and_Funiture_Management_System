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
import { OrderManagementDashboardComponent } from './components/order-managment/order-managemnt-dashboard/order-managemnt-dashboard.component';
import { QuotationManagementComponent } from './components/order-managment/quotation-management/quotation-management.component';
import { ProductCategoryComponent } from './components/order-managment/product-category/product-category.component';

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
    { path: 'profile', component: ProfileComponent, data: { title: 'User Profile' } },
    { path: 'order-management', component: OrderManagementDashboardComponent, data: { title: 'Order Management' } },
    { path: 'quotation-management', component: QuotationManagementComponent, data: { title: 'Quotation Management' } },
    { path: 'product-category', component: ProductCategoryComponent, data: { title: 'Product Category' } }
];
