import { Routes } from '@angular/router';
import { roleGuard } from './guards/role.guard';
import { permissionGuard } from './guards/permission.guard';
import { Login } from './components/auth/login/login';
import { Register } from './components/auth/register/register';
import { AdminDashComponent } from './components/user-management/admin-dash/admin-dash.component';
import { EmployeeDashComponent } from './components/user-management/employee-dash/employee-dash.component';
import { ChangePassword } from './components/auth/change-password/change-password';
import { UserManagementDashboardComponent } from './components/user-management/user-management-dashboard/user-management-dashboard.component';
import { ProfileComponent } from './components/profile/profile.component';
import { EmployeeManagementComponent } from './components/user-management/employee-management/employee-management.component';
import { SupplierManagementComponent } from './components/user-management/supplier-management/supplier-management.component';
import { CustomerManagementComponent } from './components/user-management/customer-management/customer-management.component';
import { AttendanceListComponent } from './components/attendance/attendance-list/attendance-list.component';
import { AdminLayoutComponent } from './components/shared/admin-layout/admin-layout.component';
import { LoanComponent } from './components/loan/loan.component';
import { OrderManagementDashboardComponent } from './components/order-managment/order-managemnt-dashboard/order-managemnt-dashboard.component';
import { QuotationManagementComponent } from './components/order-managment/quotation-management/quotation-management.component';
import { ProductCategoryComponent } from './components/order-managment/product-category/product-category.component';
import { PayrollManagementComponent } from './components/payroll/payroll-management/payroll-management.component';
import { DesignationSalaryConfigComponent } from './components/payroll/designation-salary-config/designation-salary-config.component';
import { SuppliyerManagementDashboardComponent } from './components/user-management/suppliyer-management-dashboard/suppliyer-management-dashboard.component';
import { MySuppliesComponent } from './components/user-management/my-supplies/my-supplies.component';
import { SupplyRawMaterialComponent } from './components/inventory-management/supply-raw-material/supply-raw-material.component';
import { SupplyRawMaterialDashboardComponent } from './components/inventory-management/supply-raw-material/supply-raw-material-dashboard.component';
import { SupplyRawMaterialRequestComponent } from './components/inventory-management/supply-raw-material-request/supply-raw-material-request.component';
import { GrnInvoiceComponent } from './components/inventory-management/grn-invoice/grn-invoice.component';
import { RawMaterialCuttingComponent } from './components/inventory-management/raw-material-cutting/raw-material-cutting.component';
import { ReceiptDashboardComponent } from './components/receipts/receipt-dashboard/receipt-dashboard.component';
import { AddReceiptComponent } from './components/receipts/add-receipt/add-receipt.component';
import { ReceiptViewComponent } from './components/receipts/receipt-view/receipt-view.component';
import { ExpensesComponent } from './components/expenses/expenses.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login, data: { title: 'User Login' } },
    { path: 'register', component: Register, data: { title: 'Register New User' } },
  
    { path: 'admin-dashboard', component: AdminDashComponent, canActivate: [roleGuard], data: { title: 'Admin Dashboard', requiredRole: 'Admin' } },
    { path: 'employee-dashboard', component: EmployeeDashComponent, canActivate: [roleGuard], data: { title: 'Employee Dashboard', requiredRole: 'Employee' } },
    { path: 'user-management', component: UserManagementDashboardComponent, canActivate: [roleGuard], data: { title: 'User Management', requiredRole: 'Admin' } },
    
    // Functional routes protected by granular permissionGuard
    { path: 'employee-management', component: EmployeeManagementComponent, canActivate: [permissionGuard], data: { title: 'Employee Management', requiredFunction: 'employee-management' } },
    { path: 'supplier-management', component: SupplierManagementComponent, canActivate: [permissionGuard], data: { title: 'Supplier Management', requiredFunction: 'supplier-management' } },
    { path: 'customer-management', component: CustomerManagementComponent, canActivate: [permissionGuard], data: { title: 'Customer Management', requiredFunction: 'customer-management' } },
    
    { path: 'change-password', component: ChangePassword, canActivate: [roleGuard], data: { title: 'Change Password' } },
    { path: 'profile', component: ProfileComponent, canActivate: [roleGuard], data: { title: 'User Profile' } },
    { path: 'supplier-dashboard', component: SuppliyerManagementDashboardComponent, canActivate: [roleGuard], data: { title: 'Supplier Dashboard', requiredRole: 'Supplier' } },
    { path: 'my-supplies', component: MySuppliesComponent, canActivate: [roleGuard], data: { title: 'My Supplies', requiredRole: 'Supplier' } },
    
    { path: 'order-management', component: OrderManagementDashboardComponent, canActivate: [permissionGuard], data: { title: 'Order Management', requiredFunction: 'order-management' } },
    { path: 'quotation-management', component: QuotationManagementComponent, canActivate: [permissionGuard], data: { title: 'Quotation Management', requiredFunction: 'quotation-management' } },
    { path: 'product-category', component: ProductCategoryComponent, canActivate: [permissionGuard], data: { title: 'Product Category', requiredFunction: 'product-category' } },
    { path: 'attendance-management', component: AttendanceListComponent, canActivate: [permissionGuard], data: { title: 'Attendance Management', requiredFunction: 'attendance-management' } },
    { path: 'loan-management', component: LoanComponent, canActivate: [permissionGuard], data: { title: 'Loan & Advance Management', requiredFunction: 'loan-management' } },
    { path: 'payroll-management', component: PayrollManagementComponent, canActivate: [permissionGuard], data: { title: 'Payroll Automation', requiredFunction: 'payroll-management' } },
    { path: 'designation-salary', component: DesignationSalaryConfigComponent, canActivate: [permissionGuard], data: { title: 'Designation Salary Settings', requiredFunction: 'designation-salary' } },

    { path: 'log-management', component: SupplyRawMaterialDashboardComponent, canActivate: [permissionGuard], data: { title: 'Log Management History', requiredFunction: 'log-management' } },
    { path: 'log-management/add', component: SupplyRawMaterialComponent, canActivate: [permissionGuard], data: { title: 'Add Supply Order', requiredFunction: 'log-management' } },
    { path: 'log-management/cutting', component: RawMaterialCuttingComponent, canActivate: [permissionGuard], data: { title: 'Raw Material Cutting', requiredFunction: 'raw-material-cutting' } },
    { path: 'supply-request-management', component: SupplyRawMaterialRequestComponent, canActivate: [permissionGuard], data: { title: 'Supply Request Management', requiredFunction: 'supply-request-management' } },
    { path: 'inventory/grn-invoice/:id', component: GrnInvoiceComponent, canActivate: [permissionGuard], data: { title: 'GRN Invoice', requiredFunction: 'log-management' } },
    
    { path: 'receipts', component: ReceiptDashboardComponent, canActivate: [permissionGuard], data: { title: 'Receipt Dashboard', requiredFunction: 'receipts' } },
    { path: 'receipts/add', component: AddReceiptComponent, canActivate: [permissionGuard], data: { title: 'Add Receipt', requiredFunction: 'receipts' } },
    { path: 'receipts/view/:id', component: ReceiptViewComponent, canActivate: [permissionGuard], data: { title: 'Printable Receipt', requiredFunction: 'receipts' } },
    { path: 'expenses', component: ExpensesComponent, canActivate: [permissionGuard], data: { title: 'Expense Management', requiredFunction: 'expenses' } },
];
