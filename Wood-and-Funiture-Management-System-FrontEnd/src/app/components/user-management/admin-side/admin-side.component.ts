import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-admin-side',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './admin-side.component.html',
  styleUrls: ['./admin-side.component.css']
})
export class AdminSideComponent {
  menuItems = [
    { name: 'Dashboard', icon: 'bi-grid-1x2-fill', route: '/admin-dashboard' },
    { name: 'User Management', icon: 'bi-people-fill', route: '/user-management' },
    { name: 'Inventory', icon: 'bi-box-seam-fill', route: '/inventory' },
    { name: 'Orders', icon: 'bi-cart-fill', route: '/orders' },
    { name: 'Employees', icon: 'bi-person-badge-fill', route: '/employees' },
    { name: 'Settings', icon: 'bi-gear-fill', route: '/settings' }
  ];
}
