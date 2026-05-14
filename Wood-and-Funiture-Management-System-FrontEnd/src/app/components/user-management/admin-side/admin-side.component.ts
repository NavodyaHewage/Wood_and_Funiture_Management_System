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
  menuItems = [
    { name: 'Dashboard', icon: 'bi-grid-1x2-fill', route: '/admin-dashboard' },
    { name: 'User Management', icon: 'bi-people-fill', route: '/user-management' },
    { name: 'Employees', icon: 'bi-person-badge-fill', route: '/employee-management' },
    { name: 'Attendance', icon: 'bi-calendar-check-fill', route: '/attendance-management' },
    { name: 'Loans And Advances', icon: 'bi-bank', route: '/loan-management' },
    { name: 'Payroll Automation', icon: 'bi-wallet2', route: '/payroll-management' },
    { name: 'Designation Rates', icon: 'bi-currency-exchange', route: '/designation-salary' },
    { name: 'Suppliers', icon: 'bi-truck', route: '/supplier-management' },
    { name: 'Customers', icon: 'bi-person-heart', route: '/customer-management' },
    { name: 'Product Category', icon: 'bi-tags-fill', route: '/product-category' },
    { name: 'Log Management', icon: 'bi bi-tree-fill me-2', route: '/log-management' },
    { name: 'Quotations', icon: 'bi-file-earmark-text-fill', route: '/quotation-management' },
    { name: 'Orders', icon: 'bi-cart-fill', route: '/order-management' },
    { name: 'Inventory', icon: 'bi-box-seam-fill', route: '/inventory' },
    { name: 'Supply Requests', icon: 'bi-clipboard-check-fill', route: '/supply-request-management' },
    { name: 'Settings', icon: 'bi-gear-fill', route: '/settings' }
  ];

  ngOnInit(): void {}
}
