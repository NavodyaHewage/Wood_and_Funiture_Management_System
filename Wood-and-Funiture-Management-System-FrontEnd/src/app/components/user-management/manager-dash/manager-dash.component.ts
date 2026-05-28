import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { PermissionService } from '../../../service/permission.service';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';

export interface DashboardCard {
  id: string;
  name: string;
  route: string;
  icon: string;
  description: string;
  category: string;
}

@Component({
  selector: 'app-manager-dash',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSideComponent, HeaderComponent],
  templateUrl: './manager-dash.component.html',
  styleUrls: ['./manager-dash.component.css']
})
export class ManagerDashComponent implements OnInit {
  currentUser: any;
  allowedCards: DashboardCard[] = [];

  private allCards: DashboardCard[] = [
    { id: 'employee-management', name: 'Employees', route: '/employee-management', icon: 'bi-person-badge', description: 'Manage employee details, profiles, and designations.', category: 'USER MANAGEMENT' },
    { id: 'attendance-management', name: 'Attendance', route: '/attendance-management', icon: 'bi-calendar-check', description: 'Log and track employee daily attendance.', category: 'USER MANAGEMENT' },
    { id: 'loan-management', name: 'Loans & Advances', route: '/loan-management', icon: 'bi-bank', description: 'Approve and monitor employee loans and advances.', category: 'USER MANAGEMENT' },
    { id: 'payroll-management', name: 'Payroll Automation', route: '/payroll-management', icon: 'bi-wallet2', description: 'Process payroll, salary, and generate paysheets.', category: 'USER MANAGEMENT' },
    { id: 'designation-salary', name: 'Designation Rates', route: '/designation-salary', icon: 'bi-currency-exchange', description: 'Set salary rates for different designations.', category: 'USER MANAGEMENT' },
    
    { id: 'supplier-management', name: 'Suppliers', route: '/supplier-management', icon: 'bi-truck', description: 'Register and manage raw wood suppliers.', category: 'SUPPLY CHAIN' },
    { id: 'supply-request-management', name: 'Supply Requests', route: '/supply-request-management', icon: 'bi-clipboard-check', description: 'Create and track timber supply requests.', category: 'SUPPLY CHAIN' },
    { id: 'log-management', name: 'Log Management', route: '/log-management', icon: 'bi-tree', description: 'Track wood log supply, timber volume, and stock.', category: 'SUPPLY CHAIN' },
    { id: 'raw-material-cutting', name: 'Material Cutting', route: '/log-management/cutting', icon: 'bi-scissors', description: 'Process wood cutting and output stock.', category: 'SUPPLY CHAIN' },
    
    { id: 'customer-management', name: 'Customers', route: '/customer-management', icon: 'bi-person-heart', description: 'Manage customer records and details.', category: 'SALES & CUSTOMERS' },
    { id: 'quotation-management', name: 'Quotations', route: '/quotation-management', icon: 'bi-file-earmark-text', description: 'Create and send quotes to customers.', category: 'SALES & CUSTOMERS' },
    { id: 'order-management', name: 'Orders', route: '/order-management', icon: 'bi-cart', description: 'Manage furniture production orders.', category: 'SALES & CUSTOMERS' },
    { id: 'receipts', name: 'Receipts', route: '/receipts', icon: 'bi-receipt-cutoff', description: 'Manage customer receipts and incoming payments.', category: 'SALES & CUSTOMERS' },
    
    { id: 'expenses', name: 'Expenses', route: '/expenses', icon: 'bi-credit-card', description: 'Track production, operational, and general expenses.', category: 'FINANCIALS' },
    { id: 'product-category', name: 'Product Category', route: '/product-category', icon: 'bi-tags', description: 'Manage furniture categories and wood types.', category: 'INVENTORY' }
  ];

  constructor(
    private authService: AuthService,
    private permissionService: PermissionService
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.permissionService.myPermissions$.subscribe(() => {
      this.filterDashboardCards();
    });
  }

  filterDashboardCards() {
    this.allowedCards = this.allCards.filter(card => 
      this.permissionService.hasPermission(card.id)
    );
  }

  getGroupedCards() {
    const groups: { [key: string]: DashboardCard[] } = {};
    this.allowedCards.forEach(card => {
      if (!groups[card.category]) {
        groups[card.category] = [];
      }
      groups[card.category].push(card);
    });
    return groups;
  }

  getGroupKeys(groupedObj: any) {
    return Object.keys(groupedObj);
  }
}
