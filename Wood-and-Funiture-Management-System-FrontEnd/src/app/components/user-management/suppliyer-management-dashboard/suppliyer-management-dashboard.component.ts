import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { AuthService } from '../../../service/auth.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';

interface SupplierDashboardCard {
  name: string;
  route: string;
  icon: string;
  description: string;
}

@Component({
  selector: 'app-suppliyer-management-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSideComponent, HeaderComponent, TranslatePipe],
  templateUrl: './suppliyer-management-dashboard.component.html',
  styleUrl: './suppliyer-management-dashboard.component.css'
})
export class SuppliyerManagementDashboardComponent {
  currentUser: any;

  supplierCards: SupplierDashboardCard[] = [
    {
      name: 'DASHBOARD_CARDS.SUPPLY_REQUESTS',
      route: '/supply-request-management',
      icon: 'bi-clipboard-check',
      description: 'DASHBOARD_CARDS.SUPPLY_REQUESTS_DESC_SUP'
    },
    {
      name: 'DASHBOARD_CARDS.MY_SUPPLIES',
      route: '/my-supplies',
      icon: 'bi-stack',
      description: 'DASHBOARD_CARDS.MY_SUPPLIES_DESC'
    },
    {
      name: 'DASHBOARD_CARDS.PROFILE',
      route: '/profile',
      icon: 'bi-person-circle',
      description: 'DASHBOARD_CARDS.PROFILE_DESC'
    }
  ];

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.currentUserValue;
  }
}
