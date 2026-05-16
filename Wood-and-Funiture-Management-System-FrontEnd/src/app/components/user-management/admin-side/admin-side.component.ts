import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { Observable } from 'rxjs';
import { CftCalculatorService } from '../../../service/cft-calculator.service';

@Component({
  selector: 'app-admin-side',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './admin-side.component.html',
  styleUrls: ['./admin-side.component.css']
})
export class AdminSideComponent implements OnInit {

  currentUser$: Observable<any>;

  constructor(
    private authService: AuthService,
    private cftService: CftCalculatorService
  ) {
    this.currentUser$ = this.authService.currentUser;
  }

  openCftCalculator() {
    this.cftService.open();
  }

  isSupplier(user: any): boolean {
    return user?.role?.toLowerCase() === 'supplier';
  }
  menuGroups = [
    {
      label: 'DASHBOARD',
      items: [
        { name: 'Overview', icon: 'bi-grid-1x2-fill', route: '/admin-dashboard' }
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
        { name: 'Log Management', icon: 'bi-tree-fill', route: '/log-management' }
      ]
    },
    {
      label: 'SALES & CUSTOMERS',
      items: [
        { name: 'Customers', icon: 'bi-person-heart', route: '/customer-management' },
        { name: 'Quotations', icon: 'bi-file-earmark-text-fill', route: '/quotation-management' },
        { name: 'Orders', icon: 'bi-cart-fill', route: '/order-management' }
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

  ngOnInit(): void {}
}
