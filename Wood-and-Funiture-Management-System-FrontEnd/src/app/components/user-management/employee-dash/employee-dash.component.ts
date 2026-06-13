import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { PermissionService } from '../../../service/permission.service';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';

export interface DashboardCard {
  id: string;
  name: string;
  route: string;
  icon: string;
  description: string;
  category: string;
}

@Component({
  selector: 'app-employee-dash',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSideComponent, HeaderComponent, TranslatePipe],
  templateUrl: './employee-dash.component.html',
  styleUrls: ['./employee-dash.component.css']
})
export class EmployeeDashComponent implements OnInit {
  currentUser: any;
  allowedCards: DashboardCard[] = [];

  private allCards: DashboardCard[] = [
    { id: 'employee-management', name: 'DASHBOARD_CARDS.EMPLOYEES', route: '/employee-management', icon: 'bi-person-badge', description: 'DASHBOARD_CARDS.EMPLOYEES_DESC', category: 'DASHBOARD_CARDS.CAT_USER_MGMT' },
    { id: 'attendance-management', name: 'DASHBOARD_CARDS.ATTENDANCE', route: '/attendance-management', icon: 'bi-calendar-check', description: 'DASHBOARD_CARDS.ATTENDANCE_DESC', category: 'DASHBOARD_CARDS.CAT_USER_MGMT' },
    { id: 'loan-management', name: 'DASHBOARD_CARDS.LOANS', route: '/loan-management', icon: 'bi-bank', description: 'DASHBOARD_CARDS.LOANS_DESC', category: 'DASHBOARD_CARDS.CAT_USER_MGMT' },
    { id: 'payroll-management', name: 'DASHBOARD_CARDS.PAYROLL', route: '/payroll-management', icon: 'bi-wallet2', description: 'DASHBOARD_CARDS.PAYROLL_DESC', category: 'DASHBOARD_CARDS.CAT_USER_MGMT' },
    { id: 'designation-salary', name: 'DASHBOARD_CARDS.DESIGNATION', route: '/designation-salary', icon: 'bi-currency-exchange', description: 'DASHBOARD_CARDS.DESIGNATION_DESC', category: 'DASHBOARD_CARDS.CAT_USER_MGMT' },
    
    { id: 'supplier-management', name: 'DASHBOARD_CARDS.SUPPLIERS', route: '/supplier-management', icon: 'bi-truck', description: 'DASHBOARD_CARDS.SUPPLIERS_DESC', category: 'DASHBOARD_CARDS.CAT_SUPPLY_CHAIN' },
    { id: 'supply-request-management', name: 'DASHBOARD_CARDS.SUPPLY_REQUESTS', route: '/supply-request-management', icon: 'bi-clipboard-check', description: 'DASHBOARD_CARDS.SUPPLY_REQUESTS_DESC_EMP', category: 'DASHBOARD_CARDS.CAT_SUPPLY_CHAIN' },
    { id: 'log-management', name: 'DASHBOARD_CARDS.LOG_MGMT', route: '/log-management', icon: 'bi-tree', description: 'DASHBOARD_CARDS.LOG_MGMT_DESC', category: 'DASHBOARD_CARDS.CAT_SUPPLY_CHAIN' },
    { id: 'raw-material-cutting', name: 'DASHBOARD_CARDS.MATERIAL_CUTTING', route: '/log-management/cutting', icon: 'bi-scissors', description: 'DASHBOARD_CARDS.MATERIAL_CUTTING_DESC', category: 'DASHBOARD_CARDS.CAT_SUPPLY_CHAIN' },
    
    { id: 'customer-management', name: 'DASHBOARD_CARDS.CUSTOMERS', route: '/customer-management', icon: 'bi-person-heart', description: 'DASHBOARD_CARDS.CUSTOMERS_DESC', category: 'DASHBOARD_CARDS.CAT_SALES_CUSTOMERS' },
    { id: 'quotation-management', name: 'DASHBOARD_CARDS.QUOTATIONS', route: '/quotation-management', icon: 'bi-file-earmark-text', description: 'DASHBOARD_CARDS.QUOTATIONS_DESC', category: 'DASHBOARD_CARDS.CAT_SALES_CUSTOMERS' },
    { id: 'order-management', name: 'DASHBOARD_CARDS.ORDERS', route: '/order-management', icon: 'bi-cart', description: 'DASHBOARD_CARDS.ORDERS_DESC', category: 'DASHBOARD_CARDS.CAT_SALES_CUSTOMERS' },
    { id: 'receipts', name: 'DASHBOARD_CARDS.RECEIPTS', route: '/receipts', icon: 'bi-receipt-cutoff', description: 'DASHBOARD_CARDS.RECEIPTS_DESC', category: 'DASHBOARD_CARDS.CAT_SALES_CUSTOMERS' },
    
    { id: 'expenses', name: 'DASHBOARD_CARDS.EXPENSES', route: '/expenses', icon: 'bi-credit-card', description: 'DASHBOARD_CARDS.EXPENSES_DESC', category: 'DASHBOARD_CARDS.CAT_FINANCIALS' },
    { id: 'product-category', name: 'DASHBOARD_CARDS.PRODUCT_CATEGORY', route: '/product-category', icon: 'bi-tags', description: 'DASHBOARD_CARDS.PRODUCT_CATEGORY_DESC', category: 'DASHBOARD_CARDS.CAT_INVENTORY' }
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
