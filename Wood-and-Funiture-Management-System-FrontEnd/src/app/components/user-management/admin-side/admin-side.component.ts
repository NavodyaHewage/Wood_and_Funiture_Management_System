import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { PermissionService } from '../../../service/permission.service';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CftCalculatorService } from '../../../service/cft-calculator.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-admin-side',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './admin-side.component.html',
  styleUrls: ['./admin-side.component.css']
})
export class AdminSideComponent implements OnInit {

  currentUser$: Observable<any>;
  filteredMenuGroups$: Observable<any[]>;

  constructor(
    private authService: AuthService,
    private cftService: CftCalculatorService,
    private permissionService: PermissionService
  ) {
    this.currentUser$ = this.authService.currentUser;
    this.filteredMenuGroups$ = combineLatest([
      this.authService.currentUser,
      this.permissionService.myPermissions$
    ]).pipe(
      map(([user, permissions]) => this.buildFilteredMenuGroups(user, permissions))
    );
  }

  openCftCalculator() {
    this.cftService.open();
  }

  isSupplier(user: any): boolean {
    return this.authService.normalizeRole(user?.role) === 'supplier';
  }

  menuGroups = [
    {
      label: 'DASHBOARD',
      items: [
        { name: 'Overview', icon: 'bi-grid-1x2-fill', route: '/admin-dashboard' },
        { name: 'Employee Overview', icon: 'bi-grid-1x2-fill', route: '/employee-dashboard' }
      ]
    },
    {
      label: 'USER MANAGEMENT',
      items: [
        { name: 'User Access', icon: 'bi-people-fill', route: '/user-management' },
        { name: 'Employees', icon: 'bi-person-badge-fill', route: '/employee-management' },
        { name: 'Attendance', icon: 'bi-calendar-check-fill', route: '/attendance-management' },
        { name: 'Loans & Advances', icon: 'bi-bank', route: '/loan-management' },
        { name: 'Payroll Automation', icon: 'bi-wallet2', route: '/payroll-management' },
        { name: 'Designation Rates', icon: 'bi-currency-exchange', route: '/designation-salary' }
      ]
    },
    {
      label: 'SUPPLY CHAIN',
      items: [
        { name: 'Suppliers', icon: 'bi-truck', route: '/supplier-management' },
        { name: 'Supply Requests', icon: 'bi-clipboard-check-fill', route: '/supply-request-management' },
        { name: 'Log Management', icon: 'bi-tree-fill', route: '/log-management' },
        { name: 'Raw Material Cutting', icon: 'bi-scissors', route: '/log-management/cutting' }
      ]
    },
    {
      label: 'SALES & CUSTOMERS',
      items: [
        { name: 'Customers', icon: 'bi-person-heart', route: '/customer-management' },
        { name: 'Quotations', icon: 'bi-file-earmark-text-fill', route: '/quotation-management' },
        { name: 'Orders', icon: 'bi-cart-fill', route: '/order-management' },
        { name: 'Receipts', icon: 'bi-receipt-cutoff', route: '/receipts' }
      ]
    },
    {
      label: 'FINANCIALS',
      items: [
        { name: 'Accounts View', icon: 'bi-wallet2', route: '/accounts-dashboard' },
        { name: 'Expenses', icon: 'bi-credit-card-fill', route: '/expenses' }
      ]
    },
    {
      label: 'INVENTORY',
      items: [
        { name: 'Product Category', icon: 'bi-tags-fill', route: '/product-category' },
        { name: 'Stock Inventory', icon: 'bi-box-seam-fill', route: '/inventory' }
      ]
    }
  ];

  private buildFilteredMenuGroups(user: any, permissions: string[]): any[] {
    if (!user) return [];
    if (this.isSupplier(user)) return [];

    const userRole = this.authService.normalizeRole(user.role);
    const isEmployee = userRole === 'employee';
    const isAdmin = userRole === 'admin';
    const permissionSet = new Set(permissions.map(permission => permission.toLowerCase()));

    return this.menuGroups.map(group => {
      const filteredItems = group.items.filter(item => {
        // Special case overview routes
        if (item.route === '/admin-dashboard') {
          return !isEmployee;
        }
        if (item.route === '/employee-dashboard') {
          return isEmployee;
        }

        // User Access is Admin only
        if (item.route === '/user-management') {
          return !isEmployee;
        }

        // Map routes to permission functions
        const functionMap: { [key: string]: string } = {
          '/employee-management': 'employee-management',
          '/attendance-management': 'attendance-management',
          '/loan-management': 'loan-management',
          '/payroll-management': 'payroll-management',
          '/designation-salary': 'designation-salary',
          '/supplier-management': 'supplier-management',
          '/supply-request-management': 'supply-request-management',
          '/log-management': 'log-management',
          '/log-management/cutting': 'raw-material-cutting',
          '/customer-management': 'customer-management',
          '/quotation-management': 'quotation-management',
          '/order-management': 'order-management',
          '/receipts': 'receipts',
          '/expenses': 'expenses',
          '/accounts-dashboard': 'accounts-dashboard',
          '/product-category': 'product-category',
          '/inventory': 'stock-inventory'
        };

        const requiredFunc = functionMap[item.route];
        if (requiredFunc) {
          return isAdmin || permissionSet.has(requiredFunc);
        }
        return true;
      });

      return {
        ...group,
        items: filteredItems
      };
    }).filter(group => group.items.length > 0);
  }

  ngOnInit(): void {}
}
