import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { AuthService } from '../../../service/auth.service';

interface SupplierDashboardCard {
  name: string;
  route: string;
  icon: string;
  description: string;
}

@Component({
  selector: 'app-suppliyer-management-dashboard',
  imports: [CommonModule, RouterLink, AdminSideComponent, HeaderComponent],
  templateUrl: './suppliyer-management-dashboard.component.html',
  styleUrl: './suppliyer-management-dashboard.component.css'
})
export class SuppliyerManagementDashboardComponent {
  currentUser: any;

  supplierCards: SupplierDashboardCard[] = [
    {
      name: 'Supply Requests',
      route: '/supply-request-management',
      icon: 'bi-clipboard-check',
      description: 'Review timber requests, confirm availability, and approve supply volumes.'
    },
    {
      name: 'My Supplies',
      route: '/my-supplies',
      icon: 'bi-stack',
      description: 'View raw material supply orders recorded under your supplier account.'
    },
    {
      name: 'Profile',
      route: '/profile',
      icon: 'bi-person-circle',
      description: 'Keep your supplier account, contact, and business details up to date.'
    }
  ];

  constructor(private authService: AuthService) {
    this.currentUser = this.authService.currentUserValue;
  }
}
